package io.github.azagniotov.stubby4j.stubs;

import io.github.azagniotov.stubby4j.builders.stubs.StubResponseBuilder;
import io.github.azagniotov.stubby4j.utils.StringUtils;
import org.eclipse.jetty.http.HttpStatus.Code;
import org.junit.Test;

import java.io.File;

import static com.google.common.truth.Truth.assertThat;
import static io.github.azagniotov.stubby4j.utils.FileUtils.fileFromString;


public class StubResponseTest {

    private static final StubResponseBuilder RESPONSE_BUILDER = new StubResponseBuilder();

    @Test
    public void shouldReturnBody_WhenFileIsNull() throws Exception {

        final StubResponse stubResponse = RESPONSE_BUILDER
                .withHttpStatusCode(Code.OK)
                .withBody("this is some body")
                .build();

        final String actualResponseBody = StringUtils.newStringUtf8(stubResponse.getResponseBodyAsBytes());
        assertThat("this is some body").isEqualTo(actualResponseBody);
    }

    @Test
    public void shouldReturnBody_WhenFileIsEmpty() throws Exception {

        final StubResponse stubResponse = RESPONSE_BUILDER
                .withHttpStatusCode(Code.OK)
                .withBody("this is some body")
                .withFile(File.createTempFile("tmp", "tmp"))
                .build();

        final String actualResponseBody = StringUtils.newStringUtf8(stubResponse.getResponseBodyAsBytes());
        assertThat("this is some body").isEqualTo(actualResponseBody);
    }

    @Test
    public void shouldReturnEmptyBody_WhenFileAndBodyAreNull() throws Exception {

        final StubResponse stubResponse = RESPONSE_BUILDER
                .withHttpStatusCode(Code.OK)
                .build();

        final String actualResponseBody = StringUtils.newStringUtf8(stubResponse.getResponseBodyAsBytes());
        assertThat("").isEqualTo(actualResponseBody);
    }

    @Test
    public void shouldReturnEmptyBody_WhenBodyIsEmpty() throws Exception {

        final StubResponse stubResponse = RESPONSE_BUILDER
                .withHttpStatusCode(Code.OK)
                .withBody("")
                .build();

        final String actualResponseBody = StringUtils.newStringUtf8(stubResponse.getResponseBodyAsBytes());
        assertThat("").isEqualTo(actualResponseBody);
    }

    @Test
    public void shouldReturnEmptyBody_WhenBodyIsEmpty_AndFileIsEmpty() throws Exception {

        final StubResponse stubResponse = RESPONSE_BUILDER
                .withHttpStatusCode(Code.OK)
                .withBody("")
                .build();

        final String actualResponseBody = StringUtils.newStringUtf8(stubResponse.getResponseBodyAsBytes());
        assertThat("").isEqualTo(actualResponseBody);
    }

    @Test
    public void shouldReturnFile_WhenFileNotEmpty_AndRegardlessOfBody() throws Exception {

        final String expectedResponseBody = "content";
        final StubResponse stubResponse = RESPONSE_BUILDER
                .withHttpStatusCode(Code.OK)
                .withBody("something")
                .withFile(fileFromString(expectedResponseBody))
                .build();

        final String actualResponseBody = StringUtils.newStringUtf8(stubResponse.getResponseBodyAsBytes());
        assertThat(expectedResponseBody).isEqualTo(actualResponseBody);
    }

    @Test
    public void shouldRequireRecording_WhenBodyStartsWithHttp() throws Exception {

        final String expectedResponseBody = "http://someurl.com";
        final StubResponse stubResponse = RESPONSE_BUILDER
                .withHttpStatusCode(Code.OK)
                .withBody(expectedResponseBody)
                .build();

        assertThat(stubResponse.isRecordingRequired()).isTrue();
    }

    @Test
    public void shouldRequireRecording_WhenBodyStartsWithHttpUpperCase() throws Exception {

        final String expectedResponseBody = "HTtP://someurl.com";
        final StubResponse stubResponse = RESPONSE_BUILDER
                .withHttpStatusCode(Code.OK)
                .withBody(expectedResponseBody)
                .build();

        assertThat(stubResponse.isRecordingRequired()).isTrue();
    }

    @Test
    public void shouldNotRequireRecording_WhenBodyStartsWithHtt() throws Exception {

        final String expectedResponseBody = "htt://someurl.com";
        final StubResponse stubResponse = RESPONSE_BUILDER
                .withHttpStatusCode(Code.OK)
                .withBody(expectedResponseBody)
                .build();

        assertThat(stubResponse.isRecordingRequired()).isFalse();
    }

    @Test
    public void shouldNotRequireRecording_WhenBodyDoesnotStartWithHttp() throws Exception {

        final String expectedResponseBody = "some body content";
        final StubResponse stubResponse = RESPONSE_BUILDER
                .withHttpStatusCode(Code.OK)
                .withBody(expectedResponseBody)
                .build();

        assertThat(stubResponse.isRecordingRequired()).isFalse();
    }

    @Test
    public void shouldFindBodyTokenized_WhenBodyContainsTemplateTokens() throws Exception {

        final String body = "some body with a <% token %>";
        final StubResponse stubResponse = RESPONSE_BUILDER
                .withHttpStatusCode(Code.OK)
                .withBody(body)
                .build();

        assertThat(stubResponse.isBodyContainsTemplateTokens()).isTrue();
    }

    @Test
    public void shouldFindBodyNotTokenized_WhenRawFileIsTemplateFile() throws Exception {

        final String body = "some body";
        final StubResponse stubResponse = RESPONSE_BUILDER
                .withHttpStatusCode(Code.OK)
                .withBody(body)
                .withFile(fileFromString("file content with a <% token %>"))
                .build();

        assertThat(stubResponse.isBodyContainsTemplateTokens()).isTrue();
    }

    @Test
    public void shouldFindBodyNotTokenized_WhenRawFileNotTemplateFile() throws Exception {

        final String body = "some body";
        final StubResponse stubResponse = RESPONSE_BUILDER
                .withHttpStatusCode(Code.OK)
                .withBody(body)
                .withFile(fileFromString("file content"))
                .build();

        assertThat(stubResponse.isBodyContainsTemplateTokens()).isFalse();
    }
}
