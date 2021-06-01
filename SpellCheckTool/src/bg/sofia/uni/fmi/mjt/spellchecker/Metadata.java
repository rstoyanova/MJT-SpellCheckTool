package bg.sofia.uni.fmi.mjt.spellchecker;

public record Metadata(int characters, int words, int mistakes) {

    public String metadataToString() {
        String output = "= = = Metadata = = =\n" +
                characters + " characters, " +
                words + " words, " +
                mistakes + " spelling issue(s) found";
        return output;
    }
}
