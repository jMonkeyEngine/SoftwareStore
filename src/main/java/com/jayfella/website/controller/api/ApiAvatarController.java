package com.jayfella.website.controller.api;

import com.jayfella.website.core.ApiResponses;
import com.jayfella.website.core.ImageDownloader;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.repository.UserRepository;
import com.jayfella.website.exception.InvalidImageException;
import com.jayfella.website.http.request.user.avatar.ChangeGravatarAvatarRequest;
import com.jayfella.website.http.request.user.avatar.ChangeSystemManagedAvatarRequest;
import com.jayfella.website.http.response.SimpleApiResponse;
import com.jayfella.website.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.jayfella.website.core.ServerAdvice.KEY_USER;

@RestController
@RequestMapping("/api/avatar")
public class ApiAvatarController {

    @Autowired
    private UserRepository userRepository;
    @Autowired private ImageService imageService;

    @PostMapping(path = "/system-managed")
    public ResponseEntity updateSystemManagedAvatar(ModelMap model,
                                                    @RequestBody ChangeSystemManagedAvatarRequest csmaRequest) throws IOException {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        String usedName = (user.getName() == null || user.getName().isEmpty())
                ? user.getUsername()
                : user.getName();

        byte[] imageData = ImageDownloader.downloadUiAvatar(
                128,
                0.5f,
                2,
                usedName,
                false,
                csmaRequest.isBold(),
                csmaRequest.getBackgroundColor(),
                csmaRequest.getForegroundColor(),
                csmaRequest.isUppercase());

        return updateUserAvatar(user, imageData);
    }

    @PostMapping(path = "/gravatar")
    public ResponseEntity updateGravatarAvatar(ModelMap model,
                                               @RequestBody ChangeGravatarAvatarRequest cgaRequest) throws IOException {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        byte[] imageData = ImageDownloader.downloadGravatarAvatar(cgaRequest.getEmailHash());
        return updateUserAvatar(user, imageData);
    }

    @PostMapping(path = "/custom")
    public ResponseEntity updateCustomAvatar(@RequestParam(value = "avatar") MultipartFile multipartFile,
                                             ModelMap model) throws IOException, InvalidImageException {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        boolean isValidImage = imageService.isValidImageMultipartFile(multipartFile);

        if (isValidImage) {
            int[] dimensions = imageService.getDimensions(multipartFile);

            if (dimensions[0] > 320 || dimensions[1] > 320) {
                throw new InvalidImageException("Image dimensions must be 1280x720.");
            }
        }

        byte[] imageData = multipartFile.getBytes();
        return updateUserAvatar(user, imageData);
    }

    private ResponseEntity<?> updateUserAvatar(User user, byte[] imageData) throws IOException {

        // every time we update the avatar, we need a new ID to avoid any caching issues.
        // we're not re-using the table because we want a new ID.
        // if we don't, the image may be cached on the client browser and cause all manner of weird behavior.

        if (user.getAvatarId() != null && !user.getAvatarId().isEmpty()) {
            imageService.deleteImage(user.getAvatarId() + ImageService.IMAGE_EXT);
        }

        String newImageFilename = imageService.create(imageData);

        user.setAvatarId(imageService.removeImageExtension(newImageFilename));
        userRepository.save(user);

        return ResponseEntity.ok()
                .body(new SimpleApiResponse(newImageFilename));

    }

}
