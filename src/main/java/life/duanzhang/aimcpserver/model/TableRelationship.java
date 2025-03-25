package life.duanzhang.aimcpserver.model;

import com.google.common.collect.Sets;
import lombok.Data;

import java.util.Comparator;
import java.util.Objects;
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
        TableRelationship tableRelationship = (TableRelationship) o;
        String collect = Sets.newHashSet( tableRelationship.getSourceTable(), tableRelationship.getSourceColumn(),  tableRelationship.getTargetTable(), tableRelationship.getTargetColumn())
                .stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.joining(","));
        String thisCollect = Sets.newHashSet( this.getSourceTable(), this.getSourceColumn(), this.getTargetTable(), this.getTargetColumn())
                .stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.joining(","));
        return Objects.equals(thisCollect, collect);
    }

}