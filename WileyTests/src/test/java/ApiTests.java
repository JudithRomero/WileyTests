import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class ApiTests {
    @Test
    public void firstTest_getAutoCompleteForJavaCheckJson() throws Exception {
        try (var inputStream = new URL(autocompleteForJavaUrl).openStream()) {
            var bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            var jsonString = bufferedReader.lines().reduce("", (acc, x) -> acc + x);
            var gson = new Gson();
            var response = gson.fromJson(jsonString, WileyAutoCompleteResponse.class);
            Assert.assertEquals(response.suggestions.length, 4);
            Assert.assertTrue(Arrays.stream(response.suggestions).allMatch(x -> x.term.startsWith(termPrefix)));
            Assert.assertEquals(response.pages.length, 4);
            Assert.assertTrue(Arrays.stream(response.pages).allMatch(x -> x.title.contains(pageIndicator)));
        }
    }

    @Test(timeout = 15_000)
    public void secondTest_httpBinCheckTenSecondsDelay() throws Exception {
        var url = new URL(httpBinDelay10EndpointUrl);
        var http = (HttpURLConnection)url.openConnection();
        http.setRequestMethod("POST");
        http.setDoInput(true);
        http.connect();
        var start = System.currentTimeMillis();
        try (var stream = http.getInputStream()) {
            var answer = stream.readAllBytes();
            var elapsedMs = System.currentTimeMillis() - start;
            Assert.assertTrue(answer.length > 0);
            Assert.assertTrue(elapsedMs >= 10_000);
        }
    }

    @Test
    public void thirdTest_httpBinCheckSamplePng() throws Exception {
        try (var actual = new URL(httpBinPngUrl).openStream()) {
            var expected = getClass().getResourceAsStream("sample.png");
            Assert.assertArrayEquals(actual.readAllBytes(), expected.readAllBytes());
        }
    }

    private static final String httpBinPngUrl = "https://httpbin.org/image/png";
    private static final String httpBinDelay10EndpointUrl = "https://httpbin.org/delay/10";
    private static final String autocompleteForJavaUrl =
            "https://www.wiley.com/en-us/search/autocomplete/comp_00001H9J?term=Java";
    private static final String pageIndicator = "Wiley";
    private static final String termPrefix = "<span class=\"search-highlight\">java</span>";
}
