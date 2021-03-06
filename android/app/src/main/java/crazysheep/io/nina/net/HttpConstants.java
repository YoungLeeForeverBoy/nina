package crazysheep.io.nina.net;

/**
 * api constants
 *
 * Created by crazysheep on 16/1/22.
 */
public class HttpConstants {

    public static final String BASE_URL = "https://api.twitter.com/1.1/";
    public static final String HOST_NAME = "api.twitter.com";

    // twitter upload media url
    // see{@link http://stackoverflow.com/questions/28642239/odd-twitter-api-v1-1-returning-error46-the-twitter-rest-api-v1-is-no-longer}
    public static final String UPLOAD_MEDIA_URL = "https://upload.twitter.com/1.1/media/upload.json";
    // twitter allow image small than 3M can attach to a tweet
    public static final int MAX_UPLOAD_PHOTO_SIZE = 3 * 1024 * 1024; // 3M
    public static final int MAX_UPLOAD_VIDEO_SIZE = 15 * 1024 * 1024; // 15M

    public static final String UPLOAD_VIDEO_MEDIA_TYPE = "video/mp4";
    public static final String UPLOAD_VIDEO_COMMAND_INIT = "INIT";
    public static final String UPLOAD_VIDEO_COMMAND_APPEND = "APPEND";
    public static final String UPLOAD_VIDEO_COMMAND_FINALIZE = "FINALIZE";

    ////////////////////// http status code ///////////////////////////////

    public static final int CODE_200 = 200; // every thing is OK
    public static final int CODE_201 = 201; // Created
    public static final int CODE_202 = 202; // Accepted
    public static final int CODE_203 = 203; // Non-Authoritative Information
    public static final int CODE_204 = 204; // No Content
    public static final int CODE_205 = 205; // Reset Content
    public static final int CODE_206 = 206; // Partial Content

    public static boolean is2xx(int code) {
        switch (code) {
            case CODE_200:
            case CODE_201:
            case CODE_202:
            case CODE_203:
            case CODE_204:
            case CODE_205:
            case CODE_206:
                return true;
        }

        return false;
    }

    public static final int code_404 = 404; // Not Found
    public static final int code_403 = 403; // Forbidden

    ////////////////////// twitter api request header params //////////////////////////

    // generate at twitter offical website
    public static final String NINA_CONSUMER_KEY = "7QZaDRVbHnQUaAwQUAGTVYFsd";
    public static final String NINA_CONSUMER_SECRET = "X8msK0MvFLLQdrq9ifx0sU9uxXWCzAS8Y2YcweFSV4ISEFrA1v";

    public static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";
    public static final String OAUTH_CONSUMER_SECRET = "oauth_consumer_secret";
    public static final String OAUTH_TOKEN = "oauth_token";
    public static final String OAUTH_TIMESTAMP = "oauth_timestamp";
    public static final String OAUTH_TOKEN_SECRET = "oauth_token_secret";
    public static final String OAUTH_NONCE = "oauth_nonce";
    public static final String OAUTH_SIGNATURE = "oauth_signature";
    public static final String OAUTH_SIGNATURE_METHOD = "oauth_signature_method";
    public static final String OAUTH_VERSION = "oauth_version";

    ///////// cert /////////////

    public static String TWITTER_CRT = "-----BEGIN CERTIFICATE-----\n" +
            "MIIFMjCCBBqgAwIBAgIQcEcYDqLflQFsrqPJWuxDRTANBgkqhkiG9w0BAQUFADCB\n" +
            "tTELMAkGA1UEBhMCVVMxFzAVBgNVBAoTDlZlcmlTaWduLCBJbmMuMR8wHQYDVQQL\n" +
            "ExZWZXJpU2lnbiBUcnVzdCBOZXR3b3JrMTswOQYDVQQLEzJUZXJtcyBvZiB1c2Ug\n" +
            "YXQgaHR0cHM6Ly93d3cudmVyaXNpZ24uY29tL3JwYSAoYykxMDEvMC0GA1UEAxMm\n" +
            "VmVyaVNpZ24gQ2xhc3MgMyBTZWN1cmUgU2VydmVyIENBIC0gRzMwHhcNMTQwODAz\n" +
            "MDAwMDAwWhcNMTYxMjMxMjM1OTU5WjCBhzELMAkGA1UEBhMCVVMxEzARBgNVBAgT\n" +
            "CkNhbGlmb3JuaWExFjAUBgNVBAcUDVNhbiBGcmFuY2lzY28xFjAUBgNVBAoUDVR3\n" +
            "aXR0ZXIsIEluYy4xGTAXBgNVBAsUEFR3aXR0ZXIgU2VjdXJpdHkxGDAWBgNVBAMU\n" +
            "D2FwaS50d2l0dGVyLmNvbTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEB\n" +
            "ALnWl0Y70VybuF1mlgSzQB5GcrgYu74P7uHS4RUbywkoSWVohux9QtjSFoZaQEFF\n" +
            "2orIWDHhDuRKVBtu291Gh3Z61tWj0z2FHqr1Y/RNao3LyWx822t17FL5Geo3tKQ1\n" +
            "aN7l2Asl8V0MQo9ZBnPdfvkRDkpdPs3F/CW8f5JfK/ixdkbiKBUjWyd/uPApIuXd\n" +
            "tVeRGejCTXcs0awDaZzWDQm5gSaQ5hpjUBeN8gPDZdfVpj0TwWx+HemXxqM2kWol\n" +
            "qGGdNIVc8cTj2Iy+eVP7NhjOy7vU+YYCi0glXtV4DkeHuIuwckbX6IAAzma6x9hm\n" +
            "8PRbw9OYpx9/57A5p7a7P7UCAwEAAaOCAWgwggFkMBoGA1UdEQQTMBGCD2FwaS50\n" +
            "d2l0dGVyLmNvbTAJBgNVHRMEAjAAMA4GA1UdDwEB/wQEAwIFoDAdBgNVHSUEFjAU\n" +
            "BggrBgEFBQcDAQYIKwYBBQUHAwIwZQYDVR0gBF4wXDBaBgpghkgBhvhFAQc2MEww\n" +
            "IwYIKwYBBQUHAgEWF2h0dHBzOi8vZC5zeW1jYi5jb20vY3BzMCUGCCsGAQUFBwIC\n" +
            "MBkaF2h0dHBzOi8vZC5zeW1jYi5jb20vcnBhMB8GA1UdIwQYMBaAFA1EXBZTRMGC\n" +
            "fh0gqyX0AWPYvnmlMCsGA1UdHwQkMCIwIKAeoByGGmh0dHA6Ly9zZC5zeW1jYi5j\n" +
            "b20vc2QuY3JsMFcGCCsGAQUFBwEBBEswSTAfBggrBgEFBQcwAYYTaHR0cDovL3Nk\n" +
            "LnN5bWNkLmNvbTAmBggrBgEFBQcwAoYaaHR0cDovL3NkLnN5bWNiLmNvbS9zZC5j\n" +
            "cnQwDQYJKoZIhvcNAQEFBQADggEBACql6SbpxMUO7P8SJfoC7R0crbM1aMIRkPaM\n" +
            "OfB87lkT07VLPaBsn1GFfKQ2yngrIIRbXz2Spix+w5p96yN1s6Zk3yng1vhs4Cw0\n" +
            "3efnD/4XjIlCdZjtedwDuAN21aM7wxyoCVQSCiOBiFDtjvs6yRGNB/Zf+KoC0YXl\n" +
            "PEDU0Kalw8zFrG33vifFdm8NLleNzkdb669tcrmPdVyiZQ82eTfnlGXaxrosVxDv\n" +
            "aR39/uZdN6MyaZEUf+qLU8upy6OMom7AP1aSa3QoHvsAZTEeQHqmkPJ5EazDNGDN\n" +
            "4gMVJ7xVKppxRvnggcTOX9yejnUNcaWFI9ZENG9Pe4qozkv9i5o=\n" +
            "-----END CERTIFICATE-----";
}
