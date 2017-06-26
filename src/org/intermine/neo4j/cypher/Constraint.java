package org.intermine.neo4j.cypher;

import org.intermine.pathquery.PathConstraint;

/**
 * Describes a constraint in Cypher Query.
 *
 * @author Yash Sharma
 */
public class Constraint {

    private ConstraintType type;

    private String constraint;

    private String join(String operand1, String operator, String operand2){
        return operand1 + " " + operator + " " + operand2;
    }

    Constraint(PathConstraint pathConstraint, PathTree pathTree){
        this.type = ConstraintConverter.getConstraintType(pathConstraint);
        TreeNode treeNode = pathTree.getTreeNode(pathConstraint.getPath());
        String value;
        switch (type){

            case AND:
                constraint = join(treeNode.getParent().getVariableName() + "." +
                            treeNode.getGraphicalName(),
                            "AND",
                            PathConstraint.getValue(pathConstraint));
                break;

            case NAND:
                constraint = join(treeNode.getParent().getVariableName() + "." +
                            treeNode.getGraphicalName(),
                            "NAND",
                            PathConstraint.getValue(pathConstraint));
                break;

            case NOR:
                constraint = join(treeNode.getParent().getVariableName() + "." +
                            treeNode.getGraphicalName(),
                            "NOR",
                            PathConstraint.getValue(pathConstraint));
                break;

            case OR:
                constraint = join(treeNode.getParent().getVariableName() + "." +
                            treeNode.getGraphicalName(),
                            "OR",
                            PathConstraint.getValue(pathConstraint));
                break;

            case CONTAINS:
                constraint = join(treeNode.getParent().getVariableName() + "." +
                            treeNode.getGraphicalName(),
                            "CONTAINS",
                            Helper.quoted(PathConstraint.getValue(pathConstraint)));
                break;

            case DOES_NOT_CONTAIN:
                constraint = "NOT " +
                            join(treeNode.getParent().getVariableName() + "." +
                            treeNode.getGraphicalName(),
                            "CONTAINS",
                            Helper.quoted(PathConstraint.getValue(pathConstraint)));
                break;

            case EQUALS:
                value = PathConstraint.getValue(pathConstraint);
                if(!Helper.isNumeric(value)){
                    value = Helper.quoted(value);
                }
                constraint = join(treeNode.getParent().getVariableName() + "." +
                            treeNode.getGraphicalName(),
                            "=",
                            value);
                break;

            case NOT_EQUALS:
                value = PathConstraint.getValue(pathConstraint);
                if(!Helper.isNumeric(value)){
                    value = Helper.quoted(value);
                }
                constraint = "NOT " +
                            join(treeNode.getParent().getVariableName() + "." +
                            treeNode.getGraphicalName(),
                            "<>",
                            value);
                break;

            case GREATER_THAN:
                constraint = join(treeNode.getParent().getVariableName() + "." +
                            treeNode.getGraphicalName(),
                            "=",
                            PathConstraint.getValue(pathConstraint));
                break;

            case GREATER_THAN_EQUALS:
                constraint = join(treeNode.getParent().getVariableName() + "." +
                            treeNode.getGraphicalName(),
                            ">=",
                            PathConstraint.getValue(pathConstraint));
                break;

            case LESS_THAN:
                constraint = join(treeNode.getParent().getVariableName() + "." +
                            treeNode.getGraphicalName(),
                            "<",
                            PathConstraint.getValue(pathConstraint));
                break;

            case LESS_THAN_EQUALS:
                constraint = join(treeNode.getParent().getVariableName() + "." +
                            treeNode.getGraphicalName(),
                            "<=",
                            PathConstraint.getValue(pathConstraint));
                break;

            // IS NOT EMPTY is synonymous to IS NOT NULL
            case IS_NOT_EMPTY:
            case IS_NOT_NULL:
                constraint = treeNode.getParent().getVariableName() + "." +
                            treeNode.getGraphicalName() + " " +
                            "IS NOT NULL";
                break;

            // IS EMPTY is synonymous to IS NULL
            case IS_EMPTY:
            case IS_NULL:
                constraint = treeNode.getParent().getVariableName() + "." +
                            treeNode.getGraphicalName() + " " +
                            "IS NULL";
                break;

            case LOOKUP:
                if(PathConstraint.getExtraValue(pathConstraint).equals(null)){
                    constraint = "ANY (key in keys(" + treeNode.getVariableName() +
                                ") WHERE " + treeNode.getVariableName() + "[key]=" +
                                Helper.quoted(PathConstraint.getValue(pathConstraint)) +
                                ")";
                }
                else{
                    // TO DO : Handle extra value in this case
                    constraint = "ANY (key in keys(" + treeNode.getVariableName() +
                                ") WHERE " + treeNode.getVariableName() + "[key]=" +
                                Helper.quoted(PathConstraint.getValue(pathConstraint)) +
                                ")";
                }
                break;

            case STRICT_NOT_EQUALS:

            case EXACT_MATCH:

            case EXISTS:

            case HAS:

            case IN:    // Require that the first argument is IN the second

            case NOT_IN:

            case ISA:

            case ISNT:

            case MATCHES:

            case NONE_OF:

            case ONE_OF:

            case DOES_NOT_EXIST:

            case DOES_NOT_HAVE:

            case DOES_NOT_MATCH:

            case DOES_NOT_OVERLAP:

            case OUTSIDE:

            case OVERLAPS:

            case WITHIN:

            case LIKE:

            case NOT_LIKE:

            case SOMETHING_NEW:
                this.constraint = "<NEW OPERATOR CONSTRAINT>";
                break;
        }
    }

    /**
     * Converts a cypher constraint to its string representation
     *
     * @return the string representation of the constraint
     */
    @Override
    public String toString(){
        return constraint;
    }

}
