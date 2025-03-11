package se.fulkopinglibraryweb.utils;

import se.fulkopinglibraryweb.model.Media;

public class SearchMedia extends GenericSearch<Media> {
    // No additional implementation needed since GenericSearch provides
    // all required functionality through reflection on Media fields
    // annotated with @SearchableField
}
