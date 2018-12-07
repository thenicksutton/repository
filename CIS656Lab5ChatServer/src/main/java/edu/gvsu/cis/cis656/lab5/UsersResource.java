package edu.gvsu.cis.cis656.lab5;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;


public class UsersResource extends ServerResource {

    private PresenceService presenceService = null;

    @Override
    public void doInit() {


        // Get Presence Service
        Key<PresenceServiceImpl> theKey2 = Key.create(PresenceServiceImpl.class, 1L);
        this.presenceService = ObjectifyService.ofy()
                .load()
                .key(theKey2)
                .now();

        if(presenceService == null){
            System.out.println("Making presence Service");
            presenceService = new PresenceServiceImpl();
        }

        // these are the representation types this resource can use to describe the
        // set of users with.
        getVariants().add(new Variant(MediaType.APPLICATION_JSON));

    }

    /**
     * Handle an HTTP GET.
     *
     * @param variant
     * @return
     * @throws ResourceException
     */
    @Get
    public Representation get(Variant variant) throws ResourceException {
        Representation result = null;
        if (null == this.presenceService) {
            ErrorMessage em = new ErrorMessage();
            return representError(variant, em);
        } else {

            if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {

                JSONArray userArray = new JSONArray();
                try {
                    for (RegistrationInfo r : presenceService.listRegisteredUsers()) {
                        JSONObject jsonobj = new JSONObject();
                        jsonobj.put("username", r.getUserName());
                        jsonobj.put("host", r.getHost());
                        jsonobj.put("port", r.getPort());
                        jsonobj.put("status", r.getStatus());
                        userArray.put(jsonobj);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                result = new JsonRepresentation(userArray);

            }
        }
        return result;
    }

    /**
     * Handle a POST Http request. Create a new widget
     *
     * @param entity
     * @throws ResourceException
     */
    @Post
    public Representation post(Representation entity, Variant variant)
            throws ResourceException
    {

        Representation rep = null;

        try {
            if (entity.getMediaType().equals(MediaType.APPLICATION_WWW_FORM,true))
            {
                Form form = new Form(entity);
                RegistrationInfo u = new RegistrationInfo();
                u.setUserName(form.getFirstValue("name"));
                u.setHost(form.getFirstValue("host"));
                u.setPort(Integer.parseInt(form.getFirstValue("port")));
                u.setStatus(true);

                presenceService.register(u);

                ObjectifyService.ofy().save().entity(presenceService).now();

                getResponse().setStatus(Status.SUCCESS_OK);

                rep = new StringRepresentation("");
                rep.setMediaType(MediaType.APPLICATION_JSON);
                getResponse().setEntity(rep);

            } else {
                getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            }
        } catch (Exception e) {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
        }
        return rep;
    }

    /**
     * Represent an error message in the requested format.
     *
     * @param variant
     * @param em
     * @return
     * @throws ResourceException
     */
    private Representation representError(Variant variant, ErrorMessage em)
            throws ResourceException {
        Representation result = null;
        if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
            result = new JsonRepresentation(em.toJSON());
        } else {
            result = new StringRepresentation(em.toString());
        }
        return result;
    }

    protected Representation representError(MediaType type, ErrorMessage em)
            throws ResourceException {
        Representation result = null;
        if (type.equals(MediaType.APPLICATION_JSON)) {
            result = new JsonRepresentation(em.toJSON());
        } else {
            result = new StringRepresentation(em.toString());
        }
        return result;
    }
}
