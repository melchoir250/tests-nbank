package requests.skelethon.interfaces;

import models.BaseModel;

public interface CrudEndpointInterface {
    Object post(BaseModel model);

    Object get(int id);

    Object put(int id, BaseModel model);

    Object delete(int id);
}
