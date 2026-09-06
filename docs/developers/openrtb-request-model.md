# OpenRTB request model compatibility

The request classes in `com.iab.openrtb.request` represent OpenRTB 2.6 and retain selected legacy fields for OpenRTB 2.5 interoperability. Optional fields remain unset when absent from the input; the model does not insert specification defaults.

## OpenRTB 2.6 fields

`Video.podid` and `Audio.podid` are strings, as defined in the IAB [Video](https://github.com/InteractiveAdvertisingBureau/openrtb2.x/blob/main/2.6.md#objectvideo) and [Audio](https://github.com/InteractiveAdvertisingBureau/openrtb2.x/blob/main/2.6.md#objectaudio) tables. This preserves identifiers such as `pod-001` and numeric-looking strings with leading zeros. Numeric JSON input is accepted by the existing Jackson coercion and serialized as a string. Java callers must supply a `String` to the builders and recompile against the updated model.

The separate `/openrtb2/video` endpoint uses its own numeric pod IDs, which are not these OpenRTB impression fields.

This matches the Go models in `prebid/openrtb` used by Prebid Server: [Video](https://github.com/prebid/openrtb/blob/v20.3.0/openrtb2/video.go) and [Audio](https://github.com/prebid/openrtb/blob/v20.3.0/openrtb2/audio.go). The corresponding Video type correction was merged in [prebid/openrtb#2](https://github.com/prebid/openrtb/pull/2).

The [Content](https://github.com/InteractiveAdvertisingBureau/openrtb2.x/blob/main/2.6.md#objectcontent) model includes `gtax`, `genres`, `realtime`, and `firstbroadcast`. The [Data](https://github.com/InteractiveAdvertisingBureau/openrtb2.x/blob/main/2.6.md#objectdata) model includes `cids` for extended content identifiers. These fields survive JSON decoding and encoding in site, app, and DOOH content.

In the current 2.6 specification, `content.livestream` describes scheduled versus on-demand delivery, while `content.realtime` describes whether the event is happening in real time. Earlier definitions of `livestream` described live content. The model preserves the supplied numeric value; adapters should interpret it according to the OpenRTB version they use.

## Legacy OpenRTB 2.5 fields

The following fields were already deprecated by OpenRTB 2.5 and removed in 2.6. They are retained so that a 2.5 request does not lose them when decoded and encoded through the shared model.

| Field | Purpose in 2.5 | Replacement in 2.6 |
| --- | --- | --- |
| `banner.wmax`, `banner.hmax` | Maximum banner dimensions | `banner.format` |
| `banner.wmin`, `banner.hmin` | Minimum banner dimensions | `banner.format` |
| `video.protocol` | Single supported video protocol | `video.protocols` |
| `content.videoquality` | Video production quality | `content.prodq` |

See the [IAB specification change log](https://github.com/InteractiveAdvertisingBureau/openrtb2.x/blob/main/2.6.md#appendixb). Retaining these fields does not make them OpenRTB 2.6 attributes. The shared model preserves supplied values independently of their replacements; callers producing strictly 2.6 requests should use the replacements and omit the legacy fields.

`user.language` is not a standard OpenRTB request attribute and is not included in the model.
