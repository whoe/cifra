package com.groupstp.cifra.web;

import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.gui.components.Component;

import java.util.List;

public class UIUtils {

    /**
     * enable components
     *
     * @param components Collection oo components to enable
     */
    public static void enableComponents(List<Component> components) {
        Preconditions.checkNotNullArgument(components);

        components.forEach(component -> {
            component.setEnabled(true);
            if (component.getParent() != null) {
                enableParentRecur(component.getParent());
            }
        });
    }

    /**
     * recursively make all parents of component enabled
     *
     * @param component
     */
    private static void enableParentRecur(Component component) {
        component.setEnabled(true);
        if (component.getParent() != null) {
            enableParentRecur(component.getParent());
        }
    }

    /**
     * disable components
     *
     * @param components Collection oo components to enable
     */
    public static void disableComponents(List<Component> components) {
        Preconditions.checkNotNullArgument(components);

        components.forEach(component -> {
            try {
                component.setEnabled(false);
            } catch (Exception ignore) {
            }
        });
    }

    /**
     * make components editable. ONLY for components implemented interface Component.Editable
     *
     * @param components Collection of components
     */
    public static void editableOnComponent(List<Component> components) {
        Preconditions.checkNotNullArgument(components);

        components.forEach(component -> {
            if (component instanceof Component.Editable) {
                ((Component.Editable) component).setEditable(true);
            }
        });
    }

    /**
     * make components NOT editable. ONLY for components implemented interface Component.Editable
     *
     * @param components Collection of components
     */
    public static void editableOffComponent(List<Component> components) {
        Preconditions.checkNotNullArgument(components);

        components.forEach(component -> {
            if (component instanceof Component.Editable) {
                ((Component.Editable) component).setEditable(false);
            }
        });
    }


}
