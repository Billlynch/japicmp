package japicmp.output;

import com.google.common.collect.ImmutableList;
import japicmp.config.Options;
import japicmp.model.*;
import japicmp.util.ModifierHelper;

import java.util.*;

public class OutputFilter extends Filter {
    private final Options options;

    public OutputFilter(Options options) {
        this.options = options;
    }

    public void filter(List<JApiClass> jApiClasses) {
        filter(jApiClasses, new FilterVisitor() {
            @Override
            public void visit(Iterator<JApiField> iterator, JApiField element) {
                boolean remove = false;
                if (options.isOutputOnlyModifications()) {
                    if (element.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
                        remove = true;
                    }
                }
                if (options.isOutputOnlyBinaryIncompatibleModifications()) {
                    if (element.isBinaryCompatible()) {
                        remove = true;
                    }
                }
                if (!ModifierHelper.matchesModifierLevel(element, OutputFilter.this.options.getAccessModifier())) {
                    remove = true;
                }
                if (remove) {
                    iterator.remove();
                }
            }

            @Override
            public void visit(Iterator<JApiAnnotation> iterator, JApiAnnotation element) {
                boolean remove = false;
                if (options.isOutputOnlyModifications()) {
                    if (element.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
                        remove = true;
                    }
                }
                if (options.isOutputOnlyBinaryIncompatibleModifications()) {
                    if (element.isBinaryCompatible()) {
                        remove = true;
                    }
                }
                if (remove) {
                    iterator.remove();
                }
            }

            @Override
            public void visit(JApiSuperclass jApiSuperclass) {
                // cannot remove superclass
            }

            @Override
            public void visit(Iterator<JApiImplementedInterface> iterator, JApiImplementedInterface element) {
                boolean remove = false;
                if (options.isOutputOnlyModifications()) {
                    if (element.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
                        remove = true;
                    }
                }
                if (options.isOutputOnlyBinaryIncompatibleModifications()) {
                    if (element.isBinaryCompatible()) {
                        remove = true;
                    }
                }
                if (remove) {
                    iterator.remove();
                }
            }

            @Override
            public void visit(Iterator<JApiConstructor> iterator, JApiConstructor element) {
                boolean remove = false;
                if (options.isOutputOnlyModifications()) {
                    if (element.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
                        remove = true;
                    }
                }
                if (options.isOutputOnlyBinaryIncompatibleModifications()) {
                    if (element.isBinaryCompatible()) {
                        remove = true;
                    }
                }
                if (!ModifierHelper.matchesModifierLevel(element, OutputFilter.this.options.getAccessModifier())) {
                    remove = true;
                }
                if (remove) {
                    iterator.remove();
                }
            }

            @Override
            public void visit(Iterator<JApiMethod> iterator, JApiMethod element) {
                boolean remove = false;
                if (options.isOutputOnlyModifications()) {
                    if (element.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
                        remove = true;
                    }
                }
                if (options.isOutputOnlyBinaryIncompatibleModifications()) {
                    if (element.isBinaryCompatible()) {
                        remove = true;
                    }
                }
                if (!ModifierHelper.matchesModifierLevel(element, OutputFilter.this.options.getAccessModifier())) {
                    remove = true;
                }
                if (remove) {
                    iterator.remove();
                }
            }

            @Override
            public void visit(Iterator<JApiClass> iterator, JApiClass jApiClass) {
                boolean remove = false;
                if (options.isOutputOnlyModifications()) {
                    if (jApiClass.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
                        remove = true;
                    }
                }
                if (options.isOutputOnlyBinaryIncompatibleModifications()) {
                    if (jApiClass.isBinaryCompatible()) {
                        remove = true;
                    }
                }
                if (!ModifierHelper.matchesModifierLevel(jApiClass, OutputFilter.this.options.getAccessModifier())) {
                    remove = true;
                }
                if (jApiClass.getChangeStatus() == JApiChangeStatus.MODIFIED) {
                    if (jApiClass.getMethods().size() == 0 && jApiClass.getConstructors().size() == 0 && jApiClass.getInterfaces().size() == 0 && jApiClass.getFields().size() == 0
                            && jApiClass.getAnnotations().size() == 0 && jApiClass.getAbstractModifier().getChangeStatus() == JApiChangeStatus.UNCHANGED
                            && jApiClass.getAccessModifier().getChangeStatus() == JApiChangeStatus.UNCHANGED
                            && jApiClass.getFinalModifier().getChangeStatus() == JApiChangeStatus.UNCHANGED
                            && jApiClass.getStaticModifier().getChangeStatus() == JApiChangeStatus.UNCHANGED
                            && jApiClass.getSuperclass().getChangeStatus() == JApiChangeStatus.UNCHANGED) {
                        remove = true;
                    }
                    if (options.getAccessModifier().getLevel() > AccessModifier.PRIVATE.getLevel() && options.isOutputOnlyModifications()) {
                        ImmutableList<Boolean> list = findOneChangedElement(jApiClass);
                        if (list.isEmpty()) { //filter out this class if it does not have any changed element at this filter level
                            remove = true;
                        }
                    }
                }
                if (remove) {
                    iterator.remove();
                }
            }

            private ImmutableList<Boolean> findOneChangedElement(JApiClass jApiClass) {
                final ImmutableList.Builder<Boolean> builder = ImmutableList.builder();
                Filter.filter(Arrays.asList(jApiClass), new FilterVisitor() {
                    @Override
                    public void visit(Iterator<JApiClass> iterator, JApiClass jApiClass) {
                        // ignore this case
                    }

                    @Override
                    public void visit(Iterator<JApiMethod> iterator, JApiMethod jApiMethod) {
                        if (jApiMethod.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
                            builder.add(Boolean.TRUE);
                        }
                    }

                    @Override
                    public void visit(Iterator<JApiConstructor> iterator, JApiConstructor jApiConstructor) {
                        if (jApiConstructor.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
                            builder.add(Boolean.TRUE);
                        }
                    }

                    @Override
                    public void visit(Iterator<JApiImplementedInterface> iterator, JApiImplementedInterface jApiImplementedInterface) {
                        if (jApiImplementedInterface.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
                            builder.add(Boolean.TRUE);
                        }
                    }

                    @Override
                    public void visit(Iterator<JApiField> iterator, JApiField jApiField) {
                        if (jApiField.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
                            builder.add(Boolean.TRUE);
                        }
                    }

                    @Override
                    public void visit(Iterator<JApiAnnotation> iterator, JApiAnnotation jApiAnnotation) {
                        if (jApiAnnotation.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
                            builder.add(Boolean.TRUE);
                        }
                    }

                    @Override
                    public void visit(JApiSuperclass jApiSuperclass) {
                        if (jApiSuperclass.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
                            builder.add(Boolean.TRUE);
                        }
                    }
                });
                return builder.build();
            }
        });
    }

    public static void sortClassesAndMethods(List<JApiClass> jApiClasses) {
        Collections.sort(jApiClasses, new Comparator<JApiClass>() {
            public int compare(JApiClass o1, JApiClass o2) {
                return o1.getFullyQualifiedName().compareToIgnoreCase(o2.getFullyQualifiedName());
            }
        });
        for (JApiClass jApiClass : jApiClasses) {
            Collections.sort(jApiClass.getMethods(), new Comparator<JApiMethod>() {
                public int compare(JApiMethod o1, JApiMethod o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
        }
    }
}
