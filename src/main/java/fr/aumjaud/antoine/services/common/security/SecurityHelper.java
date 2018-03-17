package fr.aumjaud.antoine.services.common.security;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.interfaces.RSAKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;
import java.util.Map;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityHelper {
	public final static  String SECURE_KEY_NAME = "secure-key";
	public final static  String AUTHORIZATION_HEADER = "Authorization";

	private static final Logger logger = LoggerFactory.getLogger(SecurityHelper.class);

	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public void checkSecureKeyAccess(String configSecureToken, String requestSecureKey) {
		if (requestSecureKey == null) {
			throw new NoAccessException("secure key not found", "Try to access with no secure key");
		}
		if (!configSecureToken.equals(requestSecureKey)) {
			throw new NoAccessException("secure key not valid", "Try to access to API with invalid key: " + requestSecureKey);
		}
	}

	private JWTVerifier jwtVerifier;
	public void checkJWTAccess(String token, String apiName) {
		if(jwtVerifier == null) {
			try {
				String publicKeyStr = String.join("\n", 
					Files.readAllLines(
						Paths.get(SecurityHelper.class.getClassLoader().getResource("jwt-public-cert.pem").toURI()), 
						Charset.defaultCharset()));

				// Load public key
				PublicKey publicKey = getRSAPublicKey(publicKeyStr);

				// Get JWT verifier
				jwtVerifier = JWT.require(Algorithm.RSA256((RSAKey)publicKey)).build(); 
			} catch (IOException | URISyntaxException e){
				throw new NoAccessException("can't read token", "Can't read token: " + e.getMessage());
			}
		}
		DecodedJWT jwt;
		try {
			jwt = jwtVerifier.verify(token);
		} catch (JWTVerificationException e){
			throw new NoAccessException("invalid JWT", "Try to access to API with invalid JWT, " + e.getMessage());
		}
		String autorization = jwt.getClaim("autorization").asString();
		if(autorization == null || (!autorization.contains("all") && !autorization.contains(apiName))) {
			throw new NoAccessException("no autorization", "Try to access to a not accessible API, autorization: " + autorization);
		}
	}

	public void checkSignature(String publicKeyStr, String payload, String signatureB64) {
		try {
			byte[] signature = Base64.getDecoder().decode(signatureB64);

			// Load public key
			PublicKey publicKey = getRSAPublicKey(publicKeyStr);

			// Verify the signature using the public key and SHA1 digest.
			Signature sig = Signature.getInstance("SHA1withRSA");
			sig.initVerify(publicKey);
			sig.update(payload.getBytes("utf-8"));
			if (!sig.verify(signature))
				throw new NoAccessException("signature is not valid", "Signature is not valid");

		} catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException| SignatureException e) {
			throw new NoAccessException("signature error", "Can't check signature");
		}
	}

	private PublicKey getRSAPublicKey(String publicKeyStr) {
		try {
			byte[] keyContent;
			try (PemReader pemReader = new PemReader(new StringReader(publicKeyStr))) {
				keyContent = pemReader.readPemObject().getContent();
			}
			return KeyFactory.getInstance("RSA", "BC").generatePublic(new X509EncodedKeySpec(keyContent));

		} catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException | NoSuchProviderException e) {
			throw new IllegalStateException("Can't read public key", e);
		}
	}
}
