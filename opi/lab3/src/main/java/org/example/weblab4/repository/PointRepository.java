package org.example.weblab4.repository;

import org.example.weblab4.entity.Point;
import org.example.weblab4.entity.User;
import org.example.weblab4.jooq.tables.records.PointsRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.example.weblab4.jooq.tables.Points.POINTS;

@Repository
public class PointRepository {

    private final DSLContext dsl;

    public PointRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Point save(Point point) {
        PointsRecord record = dsl.newRecord(POINTS);
        record.setX(point.getX());
        record.setY(point.getY());
        record.setR(point.getR());
        record.setResult(point.isResult());
        record.setExecutionTime(point.getExecutionTime());
        record.setCheckTime(point.getCheckTime());
        record.setUserId(point.getUserId());
        record.store();
        point.setId(record.getId());
        return point;
    }

    public List<Point> findAllByUserId(Long userId) {
        return dsl.selectFrom(POINTS)
                .where(POINTS.USER_ID.eq(userId))
                .orderBy(POINTS.CHECK_TIME.desc())
                .fetch()
                .map(r -> new Point(
                        r.getId(),
                        r.getX(),
                        r.getY(),
                        r.getR(),
                        r.getResult(),
                        r.getExecutionTime(),
                        r.getCheckTime(),
                        r.getUserId()
                ));
    }

    public void deleteAllByUserId(Long userId) {
        dsl.deleteFrom(POINTS)
                .where(POINTS.USER_ID.eq(userId))
                .execute();
    }
} 