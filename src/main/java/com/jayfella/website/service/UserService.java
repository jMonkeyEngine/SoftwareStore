package com.jayfella.website.service;

import com.jayfella.website.config.external.ServerConfig;
import com.jayfella.website.core.ImageDownloader;
import com.jayfella.website.core.RandomString;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.entity.user.UserSession;
import com.jayfella.website.database.repository.SessionRepository;
import com.jayfella.website.database.repository.UserRepository;
import com.jayfella.website.http.request.user.RegisterRequest;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.jayfella.website.core.ServerAdvice.KEY_SESSION;

@Service
public class UserService {

    private static final int USERNAME_MIN_LENGTH = 5;
    public static final int PASSWORD_MIN_LENGTH = 6;

    @Autowired private UserService passwordService;
    @Autowired private UserRepository userRepository;
    @Autowired private SessionRepository sessionRepository;

    @Autowired private ImageService imageService;

    public List<String> isValidDetails(RegisterRequest registerRequest) {
        return isValidDetails(registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getEmail());
    }

    /**
     * Determines whether the given details pass the validation tests (length, uniquety, etc).
     * @param username the users username
     * @param password the users password
     * @param email    the users email address.
     * @return a list of errors, or an empty list if no errors are found.
     */
    public List<String> isValidDetails(String username, String password, String email) {

        List<String> errors = new ArrayList<>();
        User duplicate;

        boolean usernameLengthOk = username.length() >= USERNAME_MIN_LENGTH;
        if (!usernameLengthOk) {
            errors.add("Username must be at least " + USERNAME_MIN_LENGTH + " characters in length");
        }

        if (usernameLengthOk) {
            duplicate = userRepository.findByUsernameIgnoreCase(username).orElse(null);

            if (duplicate != null) {
                errors.add("Username already exists.");
            }
        }

        boolean validEmail = EmailValidator.getInstance(false).isValid(email);
        if (!validEmail) {
            errors.add("Invalid email format.");
        }

        if (validEmail) {
            duplicate = userRepository.findByEmailIgnoreCase(email).orElse(null);

            if (duplicate != null) {
                errors.add("Email is already registered to a user.");
            }
        }

        boolean passLengthOk = password.length() >= PASSWORD_MIN_LENGTH;

        if (!passLengthOk) {
            errors.add("Password must be at least " + PASSWORD_MIN_LENGTH + " characters in length.");
        }

        return errors;
    }

    public User createUser(RegisterRequest registerRequest) throws IOException {
        return createUser(registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getPassword());
    }

    public User createUser(String username, String email, String password) throws IOException {

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        String hashAndSalt = passwordService.createNewHashAndSalt(password);
        user.setPassword(hashAndSalt);

        // https://stackoverflow.com/a/35459935
        Random random = new Random();
        String fontColor = String.format("%06x", random.nextInt(0xffffff + 1));
        String backColor = String.format("%06x", random.nextInt(0xffffff + 1));

        // create an avatar for them.
        byte[] imageData = ImageDownloader.downloadUiAvatar(
                128,
                0.5f,
                2,
                user.getUsername(),
                false,
                true,
                backColor,
                fontColor,
                true);

        String newImage = imageService.create(imageData);
        user.setAvatarId(imageService.removeImageExtension(newImage));
        userRepository.save(user);

        return user;
    }

    public String createNewHashAndSalt(String password) {
        String salt = createSalt();
        String hash = hashPassword(password, salt);
        return concatHashAndSalt(hash, salt);
    }

    /**
     * Concatenates a hash and salt ready for storage.
     * @param hash the hash of the password.
     * @param salt the salt of the password.
     * @return a concatenated string of the hash and salt.
     */
    private String concatHashAndSalt(String hash, String salt) {
        return hash + ":" + salt;
    }

    /**
     * Checks whether the given password matches the current user password.
     * @param user     the user to check.
     * @param password the password to compare
     * @return whether or not the given password matches the user password.
     */
    public boolean passwordsMatch(User user, String password) {
        String userSalt = getSalt(user);
        String newHash = hashPassword(password, userSalt);
        String userHash = getHash(user);
        return newHash.equals(userHash);
    }

    private String getHash(User user) {
        return user.getPassword().substring(0, user.getPassword().indexOf(":"));
    }

    private String getSalt(User user) {
        return user.getPassword().substring(user.getPassword().indexOf(":") + 1);
    }

    private String hashPassword(final String password, final String salt) {
        return hashPassword(password.toCharArray(), salt.getBytes(),
                ServerConfig.getInstance().getSecurityConfig().getPbkdfIterations(),
                ServerConfig.getInstance().getSecurityConfig().getPasswordHashLength());
    }

    private String hashPassword(final char[] password, final byte[] salt, final int iterations, final int keyLength) {

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
            PBEKeySpec spec = new PBEKeySpec( password, salt, iterations, keyLength );
            SecretKey key = skf.generateSecret( spec );
            byte[] res = key.getEncoded( );
            // return res;
            return toHex(res);

        } catch( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException( e );
        }
    }

    private String createSalt() {
        return createSalt(ServerConfig.getInstance().getSecurityConfig().getSaltHashLength());
    }

    private String createSalt(int length) {
        Random r = new SecureRandom();
        byte[] saltBytes = new byte[length / 8]; // 8 bits in a byte.
        r.nextBytes(saltBytes);

        return toHex(saltBytes);
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }

        return sb.toString();
    }

    public Cookie createSessionCookie(User user) {

        String sessionString = new RandomString(32).nextString();

        UserSession userSession = new UserSession();
        userSession.setSession(sessionString);
        userSession.setIpAddress("UNKNOWN_IP");
        userSession.setUserAgent("UNKNOWN_AGENT");
        userSession.setUser(user);

        // @todo expires header in the database

        sessionRepository.save(userSession);

        Cookie cookie = new Cookie(KEY_SESSION, sessionString);
        cookie.setPath("/");

        if (ServerConfig.getInstance().isHttpsEnabled()) {
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
        }

        // 86400 seconds in a day
        cookie.setMaxAge(86400 * 180); // 6 months ish

        return cookie;
    }

}
