package se.fulkopinglibraryweb.utils;

import se.fulkopinglibraryweb.model.Magazine;

public class SearchMagazine extends GenericSearch<Magazine> {
    // No additional implementation needed since GenericSearch provides
    // all required functionality through reflection on Magazine fields
    // annotated with @SearchableField
}
