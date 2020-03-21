/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.connection;

import com.vmware.vim25.SelectionSpec;
import com.vmware.vim25.TraversalSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Property collector helper.
 *
 * @since 2019 -09-19
 */
public class PropertyCollectorHelper {
    /**
     * Build all traversal spec list.
     *
     * @return the list
     */
    public List<SelectionSpec> buildAllTraversalSpec() {
        TraversalSpec rpToVm = new TraversalSpec();
        rpToVm.setName("rpToVm");
        rpToVm.setType("ResourcePool");
        rpToVm.setPath("vm");
        rpToVm.setSkip(Boolean.FALSE);

        TraversalSpec hToVm = new TraversalSpec();
        hToVm.setType("HostSystem");
        hToVm.setPath("vm");
        hToVm.setName("hToVm");
        hToVm.getSelectSet().add(SpecFactory.createSelectionSpec("VisitFolders"));
        hToVm.setSkip(Boolean.FALSE);

        TraversalSpec vAppToVM = new TraversalSpec();
        vAppToVM.setName("vAppToVM");
        vAppToVM.setType("VirtualApp");
        vAppToVM.setPath("vm");


        TraversalSpec rpToRp = new TraversalSpec();
        rpToRp.setType("ResourcePool");
        rpToRp.setPath("resourcePool");
        rpToRp.setSkip(Boolean.FALSE);
        rpToRp.setName("rpToRp");
        rpToRp.getSelectSet().add(SpecFactory.createSelectionSpec("rpToRp"));

        TraversalSpec dcToDs = new TraversalSpec();
        dcToDs.setType("Datacenter");
        dcToDs.setPath("datastore");
        dcToDs.setName("dcToDs");
        dcToDs.setSkip(Boolean.FALSE);

        TraversalSpec crToRp = new TraversalSpec();
        crToRp.setType("ComputeResource");
        crToRp.setPath("resourcePool");
        crToRp.setSkip(Boolean.FALSE);
        crToRp.setName("crToRp");
        crToRp.getSelectSet().add(SpecFactory.createSelectionSpec("rpToRp"));

        TraversalSpec vAppToRp = new TraversalSpec();
        vAppToRp.setName("vAppToRp");
        vAppToRp.setType("VirtualApp");
        vAppToRp.setPath("resourcePool");
        vAppToRp.getSelectSet().add(SpecFactory.createSelectionSpec("rpToRp"));

        TraversalSpec dcToHf = new TraversalSpec();
        dcToHf.setSkip(Boolean.FALSE);
        dcToHf.setType("Datacenter");
        dcToHf.setPath("hostFolder");
        dcToHf.setName("dcToHf");
        dcToHf.getSelectSet().add(SpecFactory.createSelectionSpec("VisitFolders"));

        TraversalSpec crToH = new TraversalSpec();
        crToH.setSkip(Boolean.FALSE);
        crToH.setType("ComputeResource");
        crToH.setPath("host");
        crToH.setName("crToH");

        TraversalSpec dcToVmf = new TraversalSpec();
        dcToVmf.setType("Datacenter");
        dcToVmf.setSkip(Boolean.FALSE);
        dcToVmf.setPath("vmFolder");
        dcToVmf.setName("dcToVmf");
        dcToVmf.getSelectSet().add(SpecFactory.createSelectionSpec("VisitFolders"));

        TraversalSpec visitFolders = new TraversalSpec();
        visitFolders.setType("Folder");
        visitFolders.setPath("childEntity");
        visitFolders.setSkip(Boolean.FALSE);
        visitFolders.setName("VisitFolders");
        List<SelectionSpec> selectionSpecs = new ArrayList<SelectionSpec>();
        selectionSpecs.add(SpecFactory.createSelectionSpec("crToRp"));
        selectionSpecs.add(SpecFactory.createSelectionSpec("crToH"));
        selectionSpecs.add(SpecFactory.createSelectionSpec("dcToVmf"));
        selectionSpecs.add(SpecFactory.createSelectionSpec("dcToHf"));
        selectionSpecs.add(SpecFactory.createSelectionSpec("vAppToRp"));
        selectionSpecs.add(SpecFactory.createSelectionSpec("vAppToVM"));
        selectionSpecs.add(SpecFactory.createSelectionSpec("dcToDs"));
        selectionSpecs.add(SpecFactory.createSelectionSpec("hToVm"));
        selectionSpecs.add(SpecFactory.createSelectionSpec("rpToVm"));
        selectionSpecs.add(SpecFactory.createSelectionSpec("VisitFolders"));

        visitFolders.getSelectSet().addAll(selectionSpecs);

        List<SelectionSpec> result = new ArrayList<>();
        result.add(visitFolders);
        result.add(crToRp);
        result.add(crToH);
        result.add(dcToVmf);
        result.add(dcToHf);
        result.add(vAppToRp);
        result.add(vAppToVM);
        result.add(dcToDs);
        result.add(hToVm);
        result.add(rpToVm);
        result.add(rpToRp);

        return result;
    }
}
