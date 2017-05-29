package fr.aumjaud.antoine.services.common.security;

import java.io.IOException;
import java.io.StringReader;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityHelper {
	public final static  String SECURE_KEY_NAME = "secure-key";

	private static final Logger logger = LoggerFactory.getLogger(SecurityHelper.class);

	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public void checkAccess(String configSecureToken, String requestSecureKey) {
		if (requestSecureKey == null) {
			throw new NoAccessException("secure key not found", "Try to access with no secure key");
		}
		if (!configSecureToken.equals(requestSecureKey)) {
			throw new NoAccessException("secure key not valid", "Try to access to API with invalid key: " + requestSecureKey);
		}
	}
	
	public void checkSignature(String publicKeyStr, String payload, String signatureB64) {
		try {
			// Obtain the Signature header value, and base64-decode it.
			byte[] signature = Base64.getDecoder().decode(signatureB64);

			// Load public key
			byte[] keyContent;
			try (PemReader pemReader = new PemReader(new StringReader(publicKeyStr))) {
				keyContent = pemReader.readPemObject().getContent();
			}
			PublicKey publicKey = KeyFactory.getInstance("RSA", "BC")
					.generatePublic(new X509EncodedKeySpec(keyContent));

			// Verify the signature using the public key and SHA1 digest.
			Signature sig = Signature.getInstance("SHA1withRSA");
			sig.initVerify(publicKey);
			sig.update(payload.getBytes("utf-8"));
			if (!sig.verify(signature))
				throw new NoAccessException("signature is not valid", "Signature is not valid");

		} catch (InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException | SignatureException
				| IOException | NoSuchProviderException e) {
			logger.error("Exception during check signature", e);
			throw new NoAccessException("signature error", "Can't check signature");
		}
	}
}
