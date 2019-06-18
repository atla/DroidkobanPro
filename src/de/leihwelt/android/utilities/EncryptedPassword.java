package de.leihwelt.android.utilities;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.*;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;

import android.util.Log;

public class EncryptedPassword {
	private String pass = "";
	private DesEncrypter encrypter = null;

	public EncryptedPassword() {
		try {
			KeySpec spec = new DESKeySpec(new String("dk12118s").getBytes());

			SecretKey key;

			key = SecretKeyFactory.getInstance("DES").generateSecret(spec);

			encrypter = new DesEncrypter(key);

		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	}

	public void set(String decryptedString) {
		if (this.encrypter != null) {
			this.pass = this.encrypter.encrypt(decryptedString);
		} else {
			Log.e("Encrypted Password", "Could not store string in EncryptedPassword");
		}
	}

	/**
	 * 
	 * @return decrypted string if possible or else empty string
	 */
	public String get() {
		if (this.encrypter != null) {
			return this.encrypter.decrypt(this.pass);
		} else {
			Log.e("Encrypted Password", "Could not get decrypted string in EncryptedPassword");
		}
		return "";
	}

	public void setEncrypted (String encryptedString){
		this.pass = encryptedString;
	}
	
	public String getEncrypted() {
		return this.pass;
	}
}
