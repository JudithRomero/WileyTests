public class WileyAutoCompleteResponse {
    public Suggestion[] suggestions;
    public Page[] pages;

    public class Page {
        public String title;
    }

    public class Suggestion {
        public String term;
    }
}
