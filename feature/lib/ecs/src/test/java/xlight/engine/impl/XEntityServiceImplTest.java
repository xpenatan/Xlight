package xlight.engine.impl;

import org.junit.Before;
import org.junit.Test;
import xlight.engine.ecs.XWorld;

public class XEntityServiceImplTest {

    XEntityServiceImpl service;

    @Before
    public void setUp() {
        XWorld world = new XECSWorldImpl();
        service = new XEntityServiceImpl(world);
    }

    @Test
    public void test() {
        // TODO
//        Truth.assertThat(service).isEqualTo(1);
    }
}