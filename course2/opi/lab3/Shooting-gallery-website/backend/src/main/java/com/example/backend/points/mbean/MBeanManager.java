package com.example.backend.points.mbean;

import javax.management.*;
import java.lang.management.ManagementFactory;

public class MBeanManager {

    public static void registerMBeans(PointsCounter counter, ShapeArea shapeArea) throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        ObjectName counterName = new ObjectName("lab3:type=PointsCounter");
        mbs.registerMBean(counter, counterName);

        ObjectName shapeAreaName = new ObjectName("lab3:type=ShapeArea");
        mbs.registerMBean(shapeArea, shapeAreaName);
    }
}
