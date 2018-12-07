package edu.gvsu.cis.cis656.lab5;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

public class UserResource extends ServerResource {

    private PresenceService presenceService = null;
    private String userid;

    @Override
    public void doInit() {

        // URL requests routed to this resource have the user id on them.
        userid = null;
        userid = (String) getRequest().getAttributes().get("id");

        Key<PresenceServiceImpl> theKey2 = Key.create(PresenceServiceImpl.class, 1L);
        this.presenceService = ObjectifyService.ofy()
                .load()
                .key(theKey2)
                .now();

        if(presenceService == null){
            System.out.println("Making presence Service");
            presenceService = new PresenceServiceImpl();
        }
        // these are the representation types this resource supports.
        getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    }

    /**
     * Handle a PUT Http Request.
     *
     * @param variant
     * @throws ResourceException
     */
    @Put
    public Representation put(Representation entity, Variant variant){
        Representation rep = null;
        try {
            if (entity.getMediaType().equals(MediaType.APPLICATION_WWW_FORM,true))
            {
                Form form = new Form(entity);
                presenceService.setStatus(userid, Boolean.parseBoolean(form.getFirstValue("status")));

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
     * Handle a DELETE Http Request. Delete an existing widget
     *
     * @param variant
     * @throws ResourceException
     */
    @Delete
    public Representation delete(Variant variant)
            throws ResourceException
    {
        Representation rep = null;
        try {
            if (null == this.presenceService) {
                ErrorMessage em = new ErrorMessage();
                rep = representError(MediaType.APPLICATION_JSON, em);
                getResponse().setEntity(rep);
                getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return rep;
            }

            rep = new JsonRepresentation("");

            // remove from PresenceService
            presenceService.unregister(userid);
            ObjectifyService.ofy().save().entity(presenceService).now();

            getResponse().setStatus(Status.SUCCESS_OK);
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
