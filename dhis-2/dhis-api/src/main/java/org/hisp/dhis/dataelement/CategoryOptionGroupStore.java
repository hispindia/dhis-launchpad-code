package org.hisp.dhis.dataelement;

import java.util.List;

import org.hisp.dhis.common.GenericNameableObjectStore;

public interface CategoryOptionGroupStore
    extends GenericNameableObjectStore<CategoryOptionGroup>
{
    List<CategoryOptionGroup> getCategoryOptionGroups( CategoryOptionGroupSet groupSet );
}
