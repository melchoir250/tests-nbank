package api.requests.skelethon.requesters;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.models.BaseModel;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.HttpRequest;
import api.requests.skelethon.interfaces.CrudEndpointInterface;

public class ValidatedCrudRequester<M extends BaseModel>
  extends HttpRequest
  implements CrudEndpointInterface {
  private final CrudRequester crudRequester;

  public ValidatedCrudRequester(RequestSpecification requestSpecification, Endpoint endpoint,
    ResponseSpecification responseSpecification) {
    super(requestSpecification, endpoint, responseSpecification);
    this.crudRequester = new CrudRequester(requestSpecification, endpoint, responseSpecification);
  }

  @SuppressWarnings("unchecked")
  public M post() {
    return (M) crudRequester.post()
      .extract()
      .as(endpoint.getResponseModel());
  }

  @SuppressWarnings("unchecked")
  public M post(BaseModel model) {
    return (M) crudRequester.post(model)
      .extract()
      .as(endpoint.getResponseModel());
  }

  @SuppressWarnings("unchecked")
  public M get() {
    return (M) crudRequester.get()
      .extract()
      .as(endpoint.getResponseModel());
  }

  @Override
  public Object get(int id) {
    return get();
  }

  @SuppressWarnings("unchecked")
  public M put(BaseModel model) {
    return (M) crudRequester.put(model)
      .extract()
      .as(endpoint.getResponseModel());
  }

  @Override
  public Object put(int id, BaseModel model) {
    return put(model);
  }

  @Override
  public Object delete(int id) {
    return crudRequester.delete(id);
  }
}
