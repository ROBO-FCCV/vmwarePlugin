/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.vsphere;

import com.vmware.vapi.std.LocalizableMessage;
import com.vmware.vcenter.ovf.LibraryItemTypes;
import com.vmware.vcenter.ovf.OvfError;
import com.vmware.vcenter.ovf.OvfInfo;
import com.vmware.vcenter.ovf.OvfMessage;
import com.vmware.vcenter.ovf.OvfWarning;
import com.vmware.vcenter.ovf.ParseIssue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * The type Ovf util.
 *
 * @since 2019 -09-19
 */
public class OvfUtil {
    private static final Logger logger = LoggerFactory.getLogger(OvfUtil.class);

    private OvfUtil() {
    }

    private static final String HEADING_ADDITIONAL_INFO = "Additional information :";

    /**
     * Display operation result.
     *
     * @param operationSucceeded the operation succeeded
     * @param operationResult the operation result
     * @param messageOnSuccess the message on success
     * @param messageOnFailure the message on failure
     */
    public static void displayOperationResult(boolean operationSucceeded, LibraryItemTypes.ResultInfo operationResult,
        String messageOnSuccess, String messageOnFailure) {
        if (operationSucceeded) {
            logger.info(messageOnSuccess);
        } else {
            logger.info(messageOnFailure);
            checkInfo(operationResult);
        }
        infoWhetherNull(operationResult);
    }

    /**
     * Info whether null.
     *
     * @param info the info
     */
    public static void infoWhetherNull(LibraryItemTypes.ResultInfo info) {
        if (info != null) {
            List<OvfWarning> warnings = info.getWarnings();
            List<OvfInfo> additionalInfo = info.getInformation();

            if (!warnings.isEmpty() || !additionalInfo.isEmpty()) {
                logger.info(HEADING_ADDITIONAL_INFO);
            }
            ovfWarning(warnings);
            ovfInfo(additionalInfo);
        }
    }

    /**
     * Check info.
     *
     * @param info the info
     */
    public static void checkInfo(LibraryItemTypes.ResultInfo info) {
        if (info != null) {
            List<OvfError> errors = info.getErrors();
            if (!errors.isEmpty()) {
                logger.info("msg: {}", HEADING_ADDITIONAL_INFO);

                for (OvfError error : errors) {
                    printOvfMessage(error._convertTo(OvfMessage.class));
                }
            }
        }
    }

    private static void ovfWarning(List<OvfWarning> warnings) {
        for (OvfWarning warning : warnings) {
            printOvfMessage(warning._convertTo(OvfMessage.class));
        }
    }

    private static void ovfInfo(List<OvfInfo> additionalInfo) {
        for (OvfInfo information : additionalInfo) {
            List<LocalizableMessage> messages = information.getMessages();

            for (LocalizableMessage message : messages) {
                logger.info("Information: {}", message.getDefaultMessage());
            }
        }
    }

    private static void printOvfMessage(OvfMessage ovfMessage) {
        if (ovfMessage.getCategory().equals(OvfMessage.Category.SERVER)) {
            List<LocalizableMessage> messages = ovfMessage
                .getError()
                ._convertTo(com.vmware.vapi.std.errors.Error.class)
                .getMessages();
            for (LocalizableMessage message : messages) {
                logger.info("Server error message: {}", message);
            }
        } else if (ovfMessage.getCategory().equals(OvfMessage.Category.VALIDATION)) {
            for (ParseIssue issue : ovfMessage.getIssues()) {
                logger.info("Issue message: {}", issue.getMessage());
            }
        } else if (ovfMessage.getCategory().equals(OvfMessage.Category.INPUT)) {
            logger.info("Input validation message: {}", ovfMessage.getMessage());
        }
    }
}
