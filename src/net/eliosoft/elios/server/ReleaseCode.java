package net.eliosoft.elios.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a release code. The valid code matches the
 * {@link net.eliosoft.elios.server.ReleaseCode#VALIDATION_PATTERN} pattern.
 *
 * @author E362200
 */
public class ReleaseCode implements Comparable<ReleaseCode> {

    /** the identifier of the release. **/
    private String code;

    /** the development suffix. **/
    private static final String DEVELOPMENT_SUFFIX = "-dev";

    /** validation pattern. **/
    public static final Pattern VALIDATION_PATTERN = Pattern
            .compile("([0-9]\\.[0-9]*)(" + DEVELOPMENT_SUFFIX + ")*");

    /**
     * Constructs an empty release code.
     */
    ReleaseCode() {
        // nothing
    }

    /**
     * Constructs a {@link ReleaseCode} with the given code.
     *
     * @param code
     *            the code non null string
     */
    ReleaseCode(final String code) {
        if (code == null) {
            throw new IllegalArgumentException("The code could not be null");
        }
        if (!VALIDATION_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException(
                    "The code must match the given pattern "
                            + VALIDATION_PATTERN.pattern());
        }

        this.code = code;
    }

    /**
     * Creates a fresh new {@link ReleaseCode}.
     *
     * @param code
     *            the code that identified the release
     * @return a newly create {@link ReleaseCode}
     */
    public static ReleaseCode create(final String code) {
        return new ReleaseCode(code);
    }

    /**
     * Returns the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ReleaseCode other = (ReleaseCode) obj;
        if (code == null) {
            if (other.code != null) {
                return false;
            }
        } else if (!code.equals(other.code)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final ReleaseCode o) {
        Double d = Double.valueOf(extractCode(this) - extractCode(o));
        if (d > 0) {
            return 1;
        }
        if (d < 0) {
            return -1;
        }
        return 0;
    }

    /**
     * Extracts a {@link Double} representation of the code.
     *
     * @param rc
     *            a {@link ReleaseCode}
     * @return a {@link Double} representation of the code
     */
    private Double extractCode(final ReleaseCode rc) {
        Matcher matcher = VALIDATION_PATTERN.matcher(rc.code);
        matcher.matches(); // always true
        Double value = Double.valueOf(matcher.group(1));
        if (rc.isUnderDevelopment()) {
            value -= 0.00000001;
        }
        return value;
    }

    /**
     * Returns true if the current ReleaseCode is associated to a release under
     * development.
     *
     * @return true if the current ReleaseCode is associated to a release under
     *         development
     */
    public boolean isUnderDevelopment() {
        return code.endsWith(DEVELOPMENT_SUFFIX);
    }

    /**
     * Returns true if the current release code is older that the given one,
     * false otherwise.
     *
     * @param code
     *            a {@link ReleaseCode} instance
     * @return true if the current release code is older than the given one,
     *         false otherwise
     */
    public boolean before(final ReleaseCode code) {
        if (this.compareTo(code) == -1) {
            return true;
        }
        if (this.compareTo(code) == 0 && isUnderDevelopment()) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if the current release code is newer than the given one,
     * false otherwise.
     *
     * @param code
     *            a {@link ReleaseCode} instance
     * @return true if the current release code is newer than the given one,
     *         false otherwise
     */
    public boolean after(final ReleaseCode code) {
        return !code.before(this);
    }
}
