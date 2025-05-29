package life.duanzhang.aimcpserver.model;

import com.google.common.collect.Sets;
import lombok.Data;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class TableRelationship {
    private String sourceTable;
    private String sourceColumn;
    private String targetTable;
    private String targetColumn;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableRelationship that = (TableRelationship) o;

        return getRelationshipString(this).equals(getRelationshipString(that));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRelationshipString(this));
    }

    private String getRelationshipString(TableRelationship relationship) {
        Set<String> values = Sets.newHashSet(
                relationship.getSourceTable(),
                relationship.getSourceColumn(),
                relationship.getTargetTable(),
                relationship.getTargetColumn()
        );
        return values.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.joining(","));
    }
}