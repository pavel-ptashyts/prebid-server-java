# Extending OpenRTB models

Applications that use Prebid Server Java as a JAR dependency can subclass the models in
`com.iab.openrtb.request` and `com.iab.openrtb.response` from their own packages. This includes the nested OpenRTB and
Native Ads objects, so an integration can extend individual parts of a request or response as well as the root object.

This is useful when reusing bidder adapters in an application that has its own request and response representation.
A subclass can carry local context or implement application-specific behavior while remaining assignable to the concrete
OpenRTB types accepted by existing APIs. Without inheritance, an unrelated wrapper cannot be passed to those APIs;
integrations must construct the Prebid model or maintain a modified copy of it.

The value classes retain their private final fields and existing builders. Their all-arguments constructors are protected
for use by subclasses, and the existing `of(...)` factories remain available. `BrandVersion` retains its existing public
constructor. The already extensible `Native` class retains its constructors and builder.

For example, a response subclass can initialize the inherited state from an existing response:

```java
public class ApplicationBidResponse extends BidResponse {

    public ApplicationBidResponse(BidResponse response) {
        super(response.getId(), response.getSeatbid(), response.getBidid(), response.getCur(),
                response.getCustomdata(), response.getNbr(), response.getExt());
    }
}
```

Subclassing does not change the behavior of the generated builders: `build()` and `toBuilder().build()` produce the
declared model type, not the application's subtype. In particular, generated `toBuilder()` methods copy backing fields,
so overriding getters alone does not implement a lazy or copy-on-write proxy. Builder inheritance and change tracking
are outside the scope of this extension point. Custom accessors used by equality, such as `Imp.bidFloor()`, may also read
backing fields directly. Subclasses must account for this when overriding getters or equality.

Applications are responsible for their subclasses' serialization, equality and state ownership. Additional getters can
become JSON properties, and mutable collections or extension nodes are not made immutable by inheritance. Constructor
signatures follow the model fields, so subclasses may need to be updated when upgrading the dependency.
