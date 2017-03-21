package fr.xephi.authme.security.crypts;

/**
 * Common supertype for encryption methods which store their salt separately from the hash.
 */
public abstract class SeparateSaltMethod implements EncryptionMethod {

    @Override
    public abstract String computeHash(String password, String salt, String name);

    @Override
    public HashedPassword computeHash(String password, String name) {
        String salt = generateSalt();
        return new HashedPassword(computeHash(password, salt, name), salt);
    }

    @Override
    public abstract String generateSalt();

    @Override
    public boolean comparePassword(String password, HashedPassword hashedPassword, String name) {
        return hashedPassword.getHash().equals(computeHash(password, hashedPassword.getSalt(), null));
    }

    @Override
    public boolean hasSeparateSalt() {
        return true;
    }

}
