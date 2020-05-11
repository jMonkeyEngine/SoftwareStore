sjcl.random.startCollectors();

/* compute PBKDF2 on the password. */
function doPbkdf2() {
	let hex = sjcl.codec.hex.fromBits;

	let password = $("#password").val();
	let salt = "C783C129 D2B7EFD1";
	let iterations = 80000;
	let keysize = 512;

	if (password.length == 0) {
		error("Need a password!");
		return;
	}

	if (salt.length === 0) {
		error("Need a salt for PBKDF2!");
		return;
	}

	let key = sjcl.misc.pbkdf2(password, salt, iterations);
	key = key.slice(0, keysize / 32);
	key = hex(key);

	// console.log(key);
	$("#hashOutput").val(key);
	$("#loginForm").submit();
}
