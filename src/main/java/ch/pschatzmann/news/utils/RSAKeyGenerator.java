package ch.pschatzmann.news.utils;

import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.pschatzmann.news.Document;

/**
 * Generates a key with the help of RSA
 * 
 * @author pschatzmann
 *
 */

public class RSAKeyGenerator implements IKeyGenerator {
	private static Logger log = LoggerFactory.getLogger(RSAKeyGenerator.class);
	private static KeyPair pair;
	// automatic key generation using RSA hashes
	static {
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(2048, new SecureRandom("This is my default salt".getBytes()));
			pair = generator.generateKeyPair();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}

	public String getKey(Document doc) {
		return generateID(doc.getContent_t(), pair.getPrivate());
	}

	protected static String generateID(String plainText, PrivateKey privateKey) {
		try {
			Signature privateSignature = Signature.getInstance("SHA256withRSA");
			privateSignature.initSign(privateKey);
			privateSignature.update(plainText.getBytes(Charset.defaultCharset()));
			byte[] signature = privateSignature.sign();
			return Base64.getEncoder().encodeToString(signature);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
