package com.example.backend.webApp;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import io.prometheus.client.hotspot.DefaultExports;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import com.example.backend.points.mbean.MBeanInitializer;
import io.prometheus.client.Gauge;

@WebServlet("/metrics")
public class MetricsServlet extends HttpServlet {

    private static final CollectorRegistry registry = CollectorRegistry.defaultRegistry;

    private static final Gauge pointsGauge = Gauge.build()
            .name("points_counter")
            .help("Total points counted")
            .register(registry);

    private static final Gauge shapeAreaGauge = Gauge.build()
            .name("shape_area")
            .help("Current shape area")
            .register(registry);

    static {
        DefaultExports.initialize(); 
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        pointsGauge.set(MBeanInitializer.getPointsCounter().getTotalPoints());
        shapeAreaGauge.set(MBeanInitializer.getShapeArea().getArea());

        resp.setContentType(TextFormat.CONTENT_TYPE_004);
        StringWriter writer = new StringWriter();
        TextFormat.write004(writer, registry.metricFamilySamples());
        resp.getWriter().write(writer.toString());
    }
}
