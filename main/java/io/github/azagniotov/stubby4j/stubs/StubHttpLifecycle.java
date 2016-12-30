package io.github.azagniotov.stubby4j.stubs;


import io.github.azagniotov.stubby4j.annotations.CoberturaIgnore;
import io.github.azagniotov.stubby4j.annotations.VisibleForTesting;
import io.github.azagniotov.stubby4j.utils.ReflectionUtils;
import io.github.azagniotov.stubby4j.utils.StringUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.azagniotov.generics.TypeSafeConverter.asCheckedLinkedList;
import static io.github.azagniotov.stubby4j.stubs.StubResponse.okResponse;


public class StubHttpLifecycle {

    private final AtomicInteger responseSequencedIdCounter = new AtomicInteger(0);

    private String completeYAML;
    private StubRequest request;
    private Object response;
    private String requestAsYAML;
    private String responseAsYAML;

    public StubHttpLifecycle() {
        response = okResponse();
    }

    public void setResponse(final Object response) {
        if (response instanceof StubResponse || response instanceof Collection) {
            this.response = response;
        } else {
            throw new IllegalArgumentException("Trying to set response of the wrong type");
        }
    }

    public StubRequest getRequest() {
        return request;
    }

    public void setRequest(final StubRequest request) {
        this.request = request;
    }

    public StubResponse getResponse(final boolean incrementSequencedResponseId) {

        if (response instanceof StubResponse) {
            return (StubResponse) response;
        }

        final List<StubResponse> stubResponses = asCheckedLinkedList(this.response, StubResponse.class);
        if (stubResponses.isEmpty()) {
            return okResponse();
        }

        if (incrementSequencedResponseId) {
            final int responseSequencedId = responseSequencedIdCounter.getAndIncrement();
            responseSequencedIdCounter.compareAndSet(stubResponses.size(), 0);
            return stubResponses.get(responseSequencedId);
        }

        return stubResponses.get(responseSequencedIdCounter.get());
    }

    public int getNextSequencedResponseId() {
        return responseSequencedIdCounter.get();
    }

    public List<StubResponse> getResponses() {

        if (response instanceof StubResponse) {
            return new LinkedList<StubResponse>() {{
                add((StubResponse) response);
            }};
        }

        return asCheckedLinkedList(this.response, StubResponse.class);
    }

    public boolean isAuthorizationRequired() {
        return request.isSecured();
    }

    @VisibleForTesting
    String getRawAuthorizationHttpHeader() {
        return request.getRawAuthorizationHttpHeader();
    }

    @VisibleForTesting
    String getStubbedAuthorizationHeaderValue(final StubAuthorizationTypes stubbedAuthorizationHeaderType) {
        return request.getStubbedAuthorizationHeaderValue(stubbedAuthorizationHeaderType);
    }

    public boolean isIncomingRequestUnauthorized(final StubHttpLifecycle assertingLifecycle) {
        final String stubbedAuthorizationHeaderValue = getStubbedAuthorizationHeaderValue(request.getStubbedAuthorizationTypeHeader());
        return !stubbedAuthorizationHeaderValue.equals(assertingLifecycle.getRawAuthorizationHttpHeader());
    }

    public String getResourceId() {
        return getResponses().get(0).getHeaders().get(StubResponse.STUBBY_RESOURCE_ID_HEADER);
    }

    public void setResourceId(final int resourceId) {
        getResponses().forEach(response -> response.addResourceIDHeader(resourceId));
    }

    /**
     * @see StubRequest#getUrl()
     */
    public String getUrl() {
        return request.getUrl();
    }

    /**
     * Do not remove this method if your IDE complains that it is unused.
     * It is used by {@link ReflectionUtils} at runtime when fetching content for Ajax response
     */
    public String getCompleteYAML() {
        return completeYAML;
    }

    public void setCompleteYAML(final String completeYAML) {
        this.completeYAML = completeYAML;
    }

    /**
     * Do not remove this method if your IDE complains that it is unused.
     * It is used by {@link ReflectionUtils} at runtime when fetching content for Ajax response
     */
    public String getRequestAsYAML() {
        return requestAsYAML;
    }

    public void setRequestAsYAML(final String requestAsYAML) {
        this.requestAsYAML = requestAsYAML;
    }

    /**
     * Do not remove this method if your IDE complains that it is unused.
     * It is used by {@link ReflectionUtils} at runtime when fetching content for Ajax response
     */
    public String getResponseAsYAML() {
        return responseAsYAML;
    }

    public void setResponseAsYAML(final String responseAsYAML) {
        this.responseAsYAML = responseAsYAML;
    }

    public String getAjaxResponseContent(final StubTypes stubType, final String propertyName) throws Exception {
        switch (stubType) {
            case REQUEST:
                return StringUtils.objectToString(ReflectionUtils.getPropertyValue(request, propertyName));
            case RESPONSE:
                return StringUtils.objectToString(ReflectionUtils.getPropertyValue(getResponse(false), propertyName));
            default:
                return StringUtils.objectToString(ReflectionUtils.getPropertyValue(this, propertyName));
        }
    }

    public String getAjaxResponseContent(final String propertyName, final int sequencedResponseId) throws Exception {
        final List<StubResponse> allResponses = getResponses();
        final StubResponse sequencedResponse = allResponses.get(sequencedResponseId);
        return StringUtils.objectToString(ReflectionUtils.getPropertyValue(sequencedResponse, propertyName));
    }

    @Override
    @CoberturaIgnore
    public int hashCode() {
        return this.request.hashCode();
    }

    @Override
    @CoberturaIgnore
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof StubHttpLifecycle)) {
            return false;
        }

        final StubHttpLifecycle that = (StubHttpLifecycle) o;
        // The 'this' is actually the incoming asserting StubRequest, the 'that' is the stubbed one
        return this.request.equals(that.request);
    }
}
