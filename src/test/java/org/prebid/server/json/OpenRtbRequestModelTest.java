package org.prebid.server.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.iab.openrtb.request.Audio;
import com.iab.openrtb.request.BidRequest;
import com.iab.openrtb.request.Content;
import com.iab.openrtb.request.Video;
import org.junit.jupiter.api.Test;
import org.prebid.server.VertxTest;

import static org.assertj.core.api.Assertions.assertThat;

public class OpenRtbRequestModelTest extends VertxTest {

    private final JacksonMapper target = jacksonMapper;

    @Test
    public void decodeValueShouldPreserveOpenRtb25BannerSizeBounds() {
        // given
        final String json = """
                {"imp":[{"banner":{"wmax":970,"hmax":250,"wmin":300,"hmin":50}}]}
                """;

        // when
        final BidRequest result = target.decodeValue(json, BidRequest.class);

        // then
        assertThat(mapper.<JsonNode>valueToTree(result)).isEqualTo(target.decodeValue(json, JsonNode.class));
    }

    @Test
    public void decodeValueShouldPreserveOpenRtb25VideoProtocol() {
        // given
        final String json = """
                {"imp":[{"video":{"protocol":3,"protocols":[2,3]}}]}
                """;

        // when
        final BidRequest result = target.decodeValue(json, BidRequest.class);

        // then
        assertThat(mapper.<JsonNode>valueToTree(result)).isEqualTo(target.decodeValue(json, JsonNode.class));
    }

    @Test
    public void decodeValueShouldPreserveOpenRtb25ContentVideoQuality() {
        // given
        final String json = "{\"site\":{\"content\":{\"videoquality\":0,\"prodq\":1}}}";

        // when
        final BidRequest result = target.decodeValue(json, BidRequest.class);

        // then
        assertThat(mapper.<JsonNode>valueToTree(result)).isEqualTo(target.decodeValue(json, JsonNode.class));
    }

    @Test
    public void decodeValueShouldPreserveVideoPodId() {
        // given
        final String json = """
                {"imp":[{"video":{"podid":"pod-001"}}]}
                """;

        // when
        final BidRequest result = target.decodeValue(json, BidRequest.class);

        // then
        assertThat(result.getImp().getFirst().getVideo().getPodid()).isEqualTo("pod-001");
        assertThat(mapper.<JsonNode>valueToTree(result)).isEqualTo(target.decodeValue(json, JsonNode.class));
    }

    @Test
    public void decodeValueShouldPreserveAudioPodId() {
        // given
        final String json = """
                {"imp":[{"audio":{"podid":"pod-001"}}]}
                """;

        // when
        final BidRequest result = target.decodeValue(json, BidRequest.class);

        // then
        assertThat(result.getImp().getFirst().getAudio().getPodid()).isEqualTo("pod-001");
        assertThat(mapper.<JsonNode>valueToTree(result)).isEqualTo(target.decodeValue(json, JsonNode.class));
    }

    @Test
    public void decodeValueShouldAcceptNumericVideoPodId() {
        // given
        final String json = "{\"podid\":123}";

        // when
        final Video result = target.decodeValue(json, Video.class);

        // then
        assertThat(result.getPodid()).isEqualTo("123");
        assertThat(target.encodeToString(result)).isEqualTo("{\"podid\":\"123\"}");
    }

    @Test
    public void decodeValueShouldPreserveLeadingZerosInVideoPodId() {
        // given
        final String json = "{\"podid\":\"00123\"}";

        // when
        final Video result = target.decodeValue(json, Video.class);

        // then
        assertThat(result.getPodid()).isEqualTo("00123");
        assertThat(target.encodeToString(result)).isEqualTo(json);
    }

    @Test
    public void decodeValueShouldPreserveLeadingZerosInAudioPodId() {
        // given
        final String json = "{\"podid\":\"00123\"}";

        // when
        final Audio result = target.decodeValue(json, Audio.class);

        // then
        assertThat(result.getPodid()).isEqualTo("00123");
        assertThat(target.encodeToString(result)).isEqualTo(json);
    }

    @Test
    public void decodeValueShouldAcceptNumericAudioPodId() {
        // given
        final String json = "{\"podid\":123}";

        // when
        final Audio result = target.decodeValue(json, Audio.class);

        // then
        assertThat(result.getPodid()).isEqualTo("123");
        assertThat(target.encodeToString(result)).isEqualTo("{\"podid\":\"123\"}");
    }

    @Test
    public void decodeValueShouldPreserveSiteContentFields() {
        // given
        final String json = """
                {"site":{"content":{
                  "gtax":9,"genres":["1","2"],"realtime":1,"firstbroadcast":0,
                  "data":[{"name":"content-provider","cids":["content-001","content-002"]}]
                }}}
                """;

        // when
        final BidRequest result = target.decodeValue(json, BidRequest.class);

        // then
        assertThat(mapper.<JsonNode>valueToTree(result))
                .isEqualTo(target.decodeValue(json, JsonNode.class));
        assertThat(result.getSite().getContent().isEmpty()).isFalse();
    }

    @Test
    public void decodeValueShouldPreserveAppContentFields() {
        // given
        final String json = """
                {"app":{"content":{
                  "gtax":9,"genres":["1","2"],"realtime":0,"firstbroadcast":1,
                  "data":[{"name":"content-provider","cids":["content-001","content-002"]}]
                }}}
                """;

        // when
        final BidRequest result = target.decodeValue(json, BidRequest.class);

        // then
        assertThat(mapper.<JsonNode>valueToTree(result)).isEqualTo(target.decodeValue(json, JsonNode.class));
    }

    @Test
    public void decodeValueShouldPreserveDoohContentFields() {
        // given
        final String json = """
                {"dooh":{"content":{
                  "gtax":9,"genres":["1","2"],"realtime":1,"firstbroadcast":1,
                  "data":[{"name":"content-provider","cids":["content-001","content-002"]}]
                }}}
                """;

        // when
        final BidRequest result = target.decodeValue(json, BidRequest.class);

        // then
        assertThat(mapper.<JsonNode>valueToTree(result)).isEqualTo(target.decodeValue(json, JsonNode.class));
    }

    @Test
    public void decodeValueShouldLeaveMissingContentFieldsUnset() {
        // when
        final Content result = target.decodeValue("{}", Content.class);

        // then
        assertThat(result.getGtax()).isNull();
        assertThat(result.getGenres()).isNull();
        assertThat(result.getRealtime()).isNull();
        assertThat(result.getFirstbroadcast()).isNull();
        assertThat(result.isEmpty()).isTrue();
        assertThat(target.encodeToString(result)).isEqualTo("{}");
    }

    @Test
    public void decodeValueShouldPreserveEmptyContentArrays() {
        // given
        final String json = "{\"genres\":[],\"data\":[{\"cids\":[]}]}";

        // when
        final Content result = target.decodeValue(json, Content.class);

        // then
        assertThat(result.getGenres()).isEmpty();
        assertThat(result.getData().getFirst().getCids()).isEmpty();
        assertThat(mapper.<JsonNode>valueToTree(result)).isEqualTo(target.decodeValue(json, JsonNode.class));
    }
}
