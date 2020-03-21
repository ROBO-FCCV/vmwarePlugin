/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.connection;

import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.LocalizedMethodFault;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.ObjectUpdate;
import com.vmware.vim25.ObjectUpdateKind;
import com.vmware.vim25.PropertyChange;
import com.vmware.vim25.PropertyChangeOp;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertyFilterUpdate;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.SelectionSpec;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.UpdateSet;
import com.vmware.vim25.VimPortType;

import cc.plugin.vmware.exception.ApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The type Service util.
 *
 * @since 2019 -09-19
 */
public class ServiceUtil {
    private static final Logger logger = LoggerFactory.getLogger(ServiceUtil.class);

    /**
     * The S tree.
     */
    static String[] sTree = {"ManagedEntity", "ComputeResource", "ClusterComputeResource", "Datacenter", "Folder",
        "HostSystem", "ResourcePool", "VirtualMachine"};

    /**
     * The C tree.
     */
    static String[] cTree = {"ComputeResource", "ClusterComputeResource"};

    /**
     * The H tree.
     */
    static String[] hTree = {"HistoryCollector", "EventHistoryCollector", "TaskHistoryCollector"};

    /**
     * The Cb.
     */
    public AppUtil cb;

    /**
     * The Connection.
     */
    public ServiceConnection connection;

    /**
     * Instantiates a new Service util.
     */
    public ServiceUtil() {
    }

    /**
     * Create service util service util.
     *
     * @return the service util
     */
    public static ServiceUtil createServiceUtil() {
        return new ServiceUtil();
    }

    /**
     * Sets connection.
     *
     * @param serviceConnection the connection
     */
    public void setConnection(ServiceConnection serviceConnection) {
        connection = serviceConnection;
    }

    /**
     * Init.
     *
     * @param cb the cb
     * @param svc the svc
     */
    public void init(AppUtil cb, ServiceConnection svc) {
        this.cb = cb;
        connection = svc;
    }

    /**
     * Type is a boolean.
     *
     * @param searchType the search type
     * @param foundType the found type
     * @return the boolean
     */
    boolean typeIsA(String searchType, String foundType) {
        boolean typeIs = false;

        if (searchType.equals(foundType)) {
            typeIs = true;
        } else if (searchType.equals("ManagedEntity")) {
            typeIs = searchTypeManaged(foundType, typeIs);
        } else if (searchType.equals("ComputeResource")) {
            typeIs = searchTypeCompute(foundType, typeIs);
        } else if (searchType.equals("HistoryCollector")) {
            typeIs = searchTypeHistory(foundType, typeIs);
        }
        return typeIs;
    }

    private boolean searchTypeManaged(String foundType, boolean typeIs) {
        for (int i = 0; i < sTree.length; ++i) {
            if (sTree[i].equals(foundType)) {
                typeIs = true;
            }
        }
        return typeIs;
    }

    private boolean searchTypeCompute(String foundType, boolean typeIs) {
        for (int i = 0; i < cTree.length; ++i) {
            if (cTree[i].equals(foundType)) {
                typeIs = true;
            }
        }
        return typeIs;
    }

    private boolean searchTypeHistory(String foundType, boolean typeIs) {
        for (int i = 0; i < hTree.length; ++i) {
            if (hTree[i].equals(foundType)) {
                typeIs = true;
            }
        }
        return typeIs;
    }

    /**
     * Gets decendent mo ref.
     *
     * @param root the root
     * @param type the type
     * @param name the name
     * @return the decendent mo ref
     */
    public ManagedObjectReference getDecendentMoRef(ManagedObjectReference root, String type, String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        String[][] typeinfo = new String[][] {new String[] {type, "name",},};

        ObjectContent[] ocary = getContentsRecursively(root, typeinfo);
        if (ocary == null || ocary.length == 0) {
            return null;
        }
        ManagedObjectReference mor = getMor(type, name, ocary);
        return mor;
    }

    private ManagedObjectReference getMor(String type, String name, ObjectContent[] ocary) {
        ManagedObjectReference mor = null;
        ObjectContent oc;
        List<DynamicProperty> propary;
        String propval;
        for (int oci = 0; oci < ocary.length; oci++) {
            oc = ocary[oci];
            propary = oc.getPropSet();
            mor = null;
            if ((type == null || typeIsA(type, oc.getObj().getType())) && propary.size() > 0) {
                propval = (String) propary.get(0).getVal();
                if (StringUtils.equalsIgnoreCase(name, propval)) {
                    mor = oc.getObj();
                    break;
                }
            }
        }
        return mor;
    }

    /**
     * Gets first decendent mo ref.
     *
     * @param root the root
     * @param type the type
     * @return the first decendent mo ref
     */
    public ManagedObjectReference getFirstDecendentMoRef(ManagedObjectReference root, String type) {
        ArrayList morlist = getDecendentMoRefs(root, type);

        ManagedObjectReference mor = null;

        if (morlist.size() > 0) {
            mor = (ManagedObjectReference) morlist.get(0);
        }

        return mor;
    }

    /**
     * Gets decendent mo refs.
     *
     * @param root the root
     * @param type the type
     * @return the decendent mo refs
     */
    public ArrayList getDecendentMoRefs(ManagedObjectReference root, String type) {
        String[][] typeinfo = new String[][] {new String[] {type, "name"},};

        ObjectContent[] ocarray = getContentsRecursively(root, typeinfo);

        ArrayList refs = new ArrayList();

        if (ocarray == null || ocarray.length == 0) {
            return refs;
        }

        for (int oci = 0; oci < ocarray.length; oci++) {
            refs.add(ocarray[oci].getObj());
        }

        return refs;
    }

    /**
     * Get all container contents object content [ ].
     *
     * @return the object content [ ]
     */
    public ObjectContent[] getAllContainerContents() {
        ObjectContent[] ocary = getContentsRecursively(null);

        return ocary;
    }

    /**
     * Get contents recursively object content [ ].
     *
     * @param root the root
     * @return the object content [ ]
     */
    public ObjectContent[] getContentsRecursively(ManagedObjectReference root) {

        String[][] typeinfo = new String[][] {new String[] {"ManagedEntity",},};

        ObjectContent[] ocary = getContentsRecursively(root, typeinfo);

        return ocary;
    }

    /**
     * Get contents recursively object content [ ].
     *
     * @param root the root
     * @param typeinfo the typeinfo
     * @return the object content [ ]
     */
    public ObjectContent[] getContentsRecursively(ManagedObjectReference root, String[][] typeinfo) {
        if (typeinfo == null || typeinfo.length == 0) {
            return null;
        }

        ManagedObjectReference usecoll = connection.getPropCol();

        ManagedObjectReference useroot = root;
        if (useroot == null) {
            useroot = connection.getServiceContent().getRootFolder();
        }

        SelectionSpec[] selectionSpecs =
            new PropertyCollectorHelper().buildAllTraversalSpec().toArray(new SelectionSpec[] {});

        List propspecary = buildPropertySpecArray(typeinfo);
        PropertyFilterSpec spec = new PropertyFilterSpec();
        spec.getPropSet().addAll(propspecary);
        ObjectSpec os = new ObjectSpec();
        os.setSkip(false);
        os.setObj(useroot);
        if (selectionSpecs != null) {
            os.getSelectSet().addAll(Arrays.asList(selectionSpecs));
        }
        spec.getObjectSet().add(os);

        List<ObjectContent> objects;
        try {
            objects = connection.getVimPort().retrieveProperties(usecoll, Arrays.asList(spec));
        } catch (InvalidPropertyFaultMsg | RuntimeFaultFaultMsg e) {
            throw new ApplicationException("vmware retrieveProperties fail ", e);
        }
        ObjectContent[] retoc = objects.toArray(new ObjectContent[0]);

        return retoc;
    }

    /**
     * Get object contents object content [ ].
     *
     * @param root the root
     * @param type the type
     * @return the object content [ ]
     */
    public ObjectContent[] getObjectContents(ManagedObjectReference root, String type) {
        String[][] typeinfo = new String[][] {new String[] {type, "name"},};
        return getContentsRecursively(root, typeinfo);
    }

    /**
     * Gets mo ref prop.
     *
     * @param objMor the obj mor
     * @param propName the prop name
     * @return the mo ref prop
     */
    public ManagedObjectReference getMoRefProp(ManagedObjectReference objMor, String propName) {
        Object props = getDynamicProperty(objMor, propName);
        if (props == null) {
            return null;
        }

        ManagedObjectReference propmor = null;
        if (Objects.nonNull(props) && !props.getClass().isArray()) {
            propmor = (ManagedObjectReference) props;
        }

        return propmor;
    }

    /**
     * Get object properties object content [ ].
     *
     * @param mobj the mobj
     * @param properties the properties
     * @return the object content [ ]
     */
    public ObjectContent[] getObjectProperties(ManagedObjectReference mobj, String[] properties) {
        if (mobj == null) {
            return null;
        }
        if (connection == null) {
            return null;
        }
        ManagedObjectReference usecoll = connection.getPropCol();

        PropertyFilterSpec spec = new PropertyFilterSpec();
        PropertySpec propertySpec = new PropertySpec();
        propertySpec.setAll(properties == null || properties.length == 0);
        propertySpec.setType(mobj.getType());
        if (properties != null) {
            propertySpec.getPathSet().addAll(Arrays.asList(properties));
        }
        spec.getPropSet().add(propertySpec);
        ObjectSpec objectSpec = new ObjectSpec();
        objectSpec.setSkip(Boolean.FALSE);
        objectSpec.setObj(mobj);
        spec.getObjectSet().add(objectSpec);

        try {
            return connection.getVimPort()
                .retrieveProperties(usecoll, Arrays.asList(spec))
                .toArray(new ObjectContent[] {});
        } catch (InvalidPropertyFaultMsg | RuntimeFaultFaultMsg e) {
            throw new ApplicationException("vmware retrieveProperties fail ", e);
        }
    }

    /**
     * Gets dynamic property.
     *
     * @param mor the mor
     * @param propertyName the property name
     * @return the dynamic property
     */
    public Object getDynamicProperty(ManagedObjectReference mor, String propertyName) {
        Object propertyValue = new Object();
        ObjectContent[] objContent = getObjectProperties(mor, new String[] {propertyName});
        if (objContent != null) {
            List<DynamicProperty> dynamicProperty = objContent[0].getPropSet();
            if (dynamicProperty != null && dynamicProperty.size() > 0) {
                Object dynamicPropertyVal = dynamicProperty.get(0).getVal();
                String dynamicPropertyName = dynamicPropertyVal.getClass().getName();
                propertyValue = getPropertyValue(dynamicPropertyVal, dynamicPropertyName);
            }
        }
        return propertyValue;
    }

    private Object getPropertyValue(Object dynamicPropertyVal, String dynamicPropertyName) {
        Object propertyValue;
        if (dynamicPropertyName.contains("ArrayOf")) {
            String methodName =
                dynamicPropertyName.substring(dynamicPropertyName.indexOf("ArrayOf") + "ArrayOf".length());
            methodName = getMethodObj(dynamicPropertyVal, methodName);
            Method getMorMethod;
            try {
                getMorMethod = dynamicPropertyVal.getClass().getDeclaredMethod(methodName, (Class[]) null);
                propertyValue = getMorMethod.invoke(dynamicPropertyVal, (Object[]) null);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
                throw new ApplicationException("vmware dynamicProperty fail", e);
            }
        } else {
            propertyValue = dynamicPropertyVal;
        }
        return propertyValue;
    }

    private String getMethodObj(Object dynamicPropertyVal, String methodName) {
        if (methodExists(dynamicPropertyVal, "get" + methodName, null)) {
            methodName = "get" + methodName;
        } else {
            methodName = "get_" + methodName.toLowerCase();
        }
        return methodName;
    }

    /**
     * Wait for task string.
     *
     * @param taskmor the taskmor
     * @return the string
     */
    public String waitForTask(ManagedObjectReference taskmor) {
        Object[] result = waitForValues(taskmor, new String[] {"info.state", "info.error"}, new String[] {"state"},
            new Object[][] {new Object[] {TaskInfoState.SUCCESS, TaskInfoState.ERROR}});
        if (Objects.equals(result[0], TaskInfoState.SUCCESS)) {
            return "success";
        } else {
            TaskInfo tinfo = (TaskInfo) getDynamicProperty(taskmor, "info");
            if (tinfo == null) {
                logger.error("tinfo is null");
                return null;
            }
            LocalizedMethodFault fault = tinfo.getError();
            String error = "Wait For Task Error Occured fault";
            if (fault != null) {
                error = handleError(fault);
            }
            return error;
        }
    }

    private String handleError(LocalizedMethodFault fault) {
        String error;
        error = "";
        if (fault.getFault() != null) {
            error = isEmpty(fault, error);
        }
        error = judgeEmpty(fault, error);
        logger.error(error);
        return error;
    }

    /**
     * Is empty string.
     *
     * @param fault the fault
     * @param error the error
     * @return the string
     */
    public String isEmpty(LocalizedMethodFault fault, String error) {
        if (StringUtils.isNotBlank(fault.getLocalizedMessage())) {
            error += "FaultReason:" + fault.getLocalizedMessage() + ";";
        }
        return error;
    }

    /**
     * Judge empty string.
     *
     * @param fault the fault
     * @param error the error
     * @return the string
     */
    public String judgeEmpty(LocalizedMethodFault fault, String error) {
        if (StringUtils.isNotBlank(fault.getLocalizedMessage())) {
            error += "Message:" + fault.getLocalizedMessage() + ";";
        }
        return error;
    }

    /**
     * Wait for values object [ ].
     *
     * @param objmor the objmor
     * @param filterProps the filter props
     * @param endWaitProps the end wait props
     * @param expectedVals the expected vals
     * @return the object [ ]
     */
    public Object[] waitForValues(ManagedObjectReference objmor, String[] filterProps, String[] endWaitProps,
        Object[][] expectedVals) {
        // version string is initially null
        String version = "";
        Object[] endVals = new Object[endWaitProps.length];
        Object[] filterVals = new Object[filterProps.length];

        PropertyFilterSpec spec = new PropertyFilterSpec();
        ObjectSpec objectSpec = new ObjectSpec();
        objectSpec.setObj(objmor);
        objectSpec.setSkip(Boolean.FALSE);
        spec.getObjectSet().add(objectSpec);

        PropertySpec propertySpec = new PropertySpec();
        propertySpec.setType(objmor.getType());
        propertySpec.getPathSet().addAll(Arrays.asList(filterProps));
        spec.getPropSet().add(propertySpec);

        ManagedObjectReference filterSpecRef;
        try {
            filterSpecRef = connection.getVimPort().createFilter(connection.getPropCol(), spec, true);
        } catch (InvalidPropertyFaultMsg | RuntimeFaultFaultMsg e) {
            throw new ApplicationException("vmware createFilter fail ", e);
        }

        boolean reached = false;

        UpdateSet updateset = null;
        List<PropertyFilterUpdate> filtupary;
        PropertyFilterUpdate filtup;
        List<ObjectUpdate> objupary;
        while (!reached) {
            boolean retry = true;
            while (retry) {
                try {
                    updateset = connection.getVimPort().waitForUpdates(connection.getPropCol(), version);
                    retry = false;
                } catch (Exception e) {
                    logger.error("Retrying2........");
                    retry = true;
                }
            }

            if (updateset == null || updateset.getFilterSet() == null) {
                continue;
            }
            version = updateset.getVersion();
            filtupary = updateset.getFilterSet();
            reached =
                iteratorFiltupary(filterProps, endWaitProps, expectedVals, endVals, filterVals, reached, filtupary);
        }
        try {
            connection.getVimPort().destroyPropertyFilter(filterSpecRef);
        } catch (RuntimeFaultFaultMsg e) {
            throw new ApplicationException("vmware destroyPropertyFilter fail ", e);
        }

        return filterVals;
    }

    private boolean iteratorFiltupary(String[] filterProps, String[] endWaitProps, Object[][] expectedVals,
        Object[] endVals, Object[] filterVals, boolean reached, List<PropertyFilterUpdate> filtupary) {
        PropertyFilterUpdate filtup;
        List<ObjectUpdate> objupary;
        for (int fi = 0; fi < filtupary.size(); fi++) {
            filtup = filtupary.get(fi);
            objupary = filtup.getObjectSet();
            iteratorObjupary(objupary, endVals, filterVals, filterProps, endWaitProps);
        }

        Object expctdval;
        for (int chgi = 0; chgi < endVals.length && !reached; chgi++) {
            for (int vali = 0; vali < expectedVals[chgi].length && !reached; vali++) {
                expctdval = expectedVals[chgi][vali];

                reached = Objects.equals(expctdval, endVals[chgi]);
            }
        }
        return reached;
    }

    private void iteratorObjupary(List<ObjectUpdate> objupary, Object[] endVals, Object[] filterVals,
        String[] filterProps, String[] endWaitProps) {
        ObjectUpdate objup;
        for (int oi = 0; oi < objupary.size(); oi++) {
            objup = objupary.get(oi);
            getPropchgary(endVals, filterVals, filterProps, endWaitProps, objup);
        }
    }

    /**
     * Gets propchgary.
     *
     * @param endVals the end vals
     * @param filterVals the filter vals
     * @param filterProps the filter props
     * @param endWaitProps the end wait props
     * @param objup the objup
     */
    public void getPropchgary(Object[] endVals, Object[] filterVals, String[] filterProps, String[] endWaitProps,
        ObjectUpdate objup) {
        if (objup.getKind() == ObjectUpdateKind.MODIFY || objup.getKind() == ObjectUpdateKind.ENTER
            || objup.getKind() == ObjectUpdateKind.LEAVE) {
            List<PropertyChange> propchgary = objup.getChangeSet();
            getValues(endVals, filterVals, filterProps, endWaitProps, propchgary);
        }
    }

    /**
     * Gets values.
     *
     * @param endVals the end vals
     * @param filterVals the filter vals
     * @param filterProps the filter props
     * @param endWaitProps the end wait props
     * @param propchgary the propchgary
     */
    public void getValues(Object[] endVals, Object[] filterVals, String[] filterProps, String[] endWaitProps,
        List<PropertyChange> propchgary) {
        PropertyChange propchg;
        for (int ci = 0; ci < propchgary.size(); ci++) {
            propchg = propchgary.get(ci);
            updateValues(endWaitProps, endVals, propchg);
            updateValues(filterProps, filterVals, propchg);
        }
    }

    /**
     * Update values.
     *
     * @param props the props
     * @param vals the vals
     * @param propchg the propchg
     */
    protected void updateValues(String[] props, Object[] vals, PropertyChange propchg) {
        for (int findi = 0; findi < props.length; findi++) {
            getPropchg(props, vals, propchg, findi);
        }
    }

    /**
     * Gets propchg.
     *
     * @param props the props
     * @param vals the vals
     * @param propchg the propchg
     * @param findi the findi
     */
    public void getPropchg(String[] props, Object[] vals, PropertyChange propchg, int findi) {
        if (findi <= props.length && propchg.getName().lastIndexOf(props[findi]) >= 0) {
            if (propchg.getOp() == PropertyChangeOp.REMOVE) {
                vals[findi] = "";
            } else {
                vals[findi] = propchg.getVal();
            }
        }
    }

    /**
     * Build property spec array list.
     *
     * @param typeinfo the typeinfo
     * @return the list
     */
    public List buildPropertySpecArray(String[][] typeinfo) {
        // Eliminate duplicates
        HashMap<Object, Object> tInfo = new HashMap<Object, Object>();
        getTi(typeinfo, tInfo);
        // Create PropertySpecs
        ArrayList pSpecs = new ArrayList();
        addpSpecs(tInfo, pSpecs);
        return pSpecs;
    }

    /**
     * Gets ti.
     *
     * @param typeinfo the typeinfo
     * @param tInfo the t info
     */
    public void getTi(String[][] typeinfo, HashMap<Object, Object> tInfo) {
        for (int ti = 0; ti < typeinfo.length; ++ti) {
            Set props = (Set) tInfo.get(typeinfo[ti][0]);
            props = propsIsNull(typeinfo, tInfo, ti, props);
            boolean typeSkipped = false;
            setTypeSkipped(typeinfo, ti, props, typeSkipped);
        }
    }

    /**
     * Props is null set.
     *
     * @param typeinfo the typeinfo
     * @param tInfo the t info
     * @param ti the ti
     * @param props the props
     * @return the set
     */
    public Set propsIsNull(String[][] typeinfo, HashMap<Object, Object> tInfo, int ti, Set props) {
        if (props == null) {
            props = new HashSet();
            tInfo.put(typeinfo[ti][0], props);
        }
        return props;
    }

    private void addpSpecs(HashMap<Object, Object> tInfo, ArrayList pSpecs) {
        for (Map.Entry<Object, Object> entry : tInfo.entrySet()) {
            String type = (String) entry.getKey();
            PropertySpec pSpec = new PropertySpec();
            Set props = (Set) entry.getValue();
            pSpec.setType(type);
            pSpec.setAll(props.isEmpty() ? Boolean.TRUE : Boolean.FALSE);
            getPi(pSpec, props);
            pSpecs.add(pSpec);

        }
    }

    /**
     * Gets pi.
     *
     * @param pSpec the p spec
     * @param props the props
     */
    public void getPi(PropertySpec pSpec, Set props) {
        for (Iterator pi = props.iterator(); pi.hasNext();) {
            String prop = (String) pi.next();
            pSpec.getPathSet().add(prop);
        }
    }

    private void setTypeSkipped(String[][] typeinfo, int ti, Set props, boolean typeSkipped) {
        for (int pi = 0; pi < typeinfo[ti].length; ++pi) {
            String prop = typeinfo[ti][pi];
            if (typeSkipped) {
                props.add(prop);
            } else {
                typeSkipped = true;
            }
        }
    }

    /**
     * Method exists boolean.
     *
     * @param obj the obj
     * @param methodName the method name
     * @param parameterTypes the parameter types
     * @return the boolean
     */
    boolean methodExists(Object obj, String methodName, Class[] parameterTypes) {
        try {
            obj.getClass().getMethod(methodName, parameterTypes);
            return true;
        } catch (NoSuchMethodException | SecurityException e) {
            logger.error("vmware methodExists fail", e);
            return false;
        }
    }

    /**
     * Gets object property.
     *
     * @param content the content
     * @param service the service
     * @param moRef the mo ref
     * @param propertyName the property name
     * @return the object property
     */
    public Object getObjectProperty(ServiceContent content, VimPortType service, ManagedObjectReference moRef,
        String propertyName) {
        return getProperties(content, service, moRef, propertyName)[0];
    }

    /**
     * Get properties object [ ].
     *
     * @param content the content
     * @param service the service
     * @param moRef the mo ref
     * @param property the property
     * @return the object [ ]
     */
    Object[] getProperties(ServiceContent content, VimPortType service, ManagedObjectReference moRef, String property) {

        PropertySpec pSpec = new PropertySpec();
        pSpec.setType(moRef.getType());
        pSpec.getPathSet().add(property);

        ObjectSpec oSpec = new ObjectSpec();
        oSpec.setObj(moRef);

        PropertyFilterSpec pfSpec = new PropertyFilterSpec();
        pfSpec.getPropSet().add(pSpec);
        pfSpec.getObjectSet().add(oSpec);

        List<PropertyFilterSpec> pfsList = new ArrayList<>();
        pfsList.add(pfSpec);
        List<ObjectContent> ocs;

        try {
            ocs = service.retrieveProperties(content.getPropertyCollector(), pfsList);
        } catch (InvalidPropertyFaultMsg | RuntimeFaultFaultMsg e) {
            logger.error(e.getMessage(), e);
            throw new ApplicationException("vmware retrieveProperties fail : " + e.getMessage(), e);
        }

        // Return value, one object for each property specified
        Object[] ret = new Object[1];

        if (ocs != null) {
            ret = iteratorOCS(ocs, ret, property);
        }
        return ret;
    }

    private Object[] iteratorOCS(List<ObjectContent> ocs, Object[] ret, String property) {
        for (int i = 0; i < ocs.size(); ++i) {
            ObjectContent oc = ocs.get(i);
            List<DynamicProperty> dps = oc.getPropSet();
            if (dps == null) {
                continue;
            }

            if (getRet(ret, property, dps)) {
                return ret;
            }
        }
        return ret;
    }

    private boolean getRet(Object[] ret, String property, List<DynamicProperty> dps) {
        for (int j = 0; j < dps.size(); ++j) {
            DynamicProperty dp = dps.get(j);
            // find property path index
            for (int p = 0; p < ret.length; ++p) {
                if (property.equals(dp.getName())) {
                    ret[p] = dp.getVal();
                    return true;
                }
            }
        }
        return false;
    }
}
