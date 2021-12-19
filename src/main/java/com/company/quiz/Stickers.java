package com.company.quiz;

public enum Stickers {
    QuestionAlreadyExists("CAACAgIAAxkBAAEDShdhkm4DsdJFl_mBL851mR8Ca_gxDwACsQ0AAjppOUjINKv7N0gdWiIE"),
    Question("CAACAgIAAxkBAAEDShdhkm4DsdJFl_mBL851mR8Ca_gxDwACsQ0AAjppOUjINKv7N0gdWiIE"),
    RightAnswer("CAACAgIAAxkBAAEDSjthkoEJoQKIsjn-1zi9UzVQFkI-jAAC4w0AArAsKUkmVocAAbI_aIAiBA"),
    Greet("CAACAgIAAxkBAAEDShFhkmhuE5lz_InXvOrrxZifKKaxYQACuwIAAqKK8QdcF8HD_GCZXyIE"),
    Score("CAACAgIAAxkBAAEDVPlhmilWc7ZzcjRMtge8ij3llCTEQAACYwQAAs7Y6Asx61tywusibCIE"),
    WrongAnswer("CAACAgIAAxkBAAEDSjNhkoAkb9KIVhJ0xTBLBn5HdDeE5QACrBIAAmCRIEnnz3aDncA0fCIE");
    public final String token;

    Stickers(final String token) {
        this.token = token;
    }
}
