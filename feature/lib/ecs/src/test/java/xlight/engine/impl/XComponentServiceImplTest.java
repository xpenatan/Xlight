package xlight.engine.impl;

import com.google.common.truth.Truth;
import org.junit.Before;
import org.junit.Test;
import xlight.engine.ecs.component.XComponentType;
import xlight.engine.esc.component.ComponentA;
import xlight.engine.esc.component.ComponentB;
import xlight.engine.esc.component.ComponentC;
import xlight.engine.esc.component.ComponentD;

public class XComponentServiceImplTest {

    XECSWorldImpl world;
    XComponentServiceImpl service;

    @Before
    public void setUp() {
        world = new XECSWorldImpl();
        XEntityServiceImpl entityService = new XEntityServiceImpl();
        service = new XComponentServiceImpl();
        service.init(world, entityService);
    }

    @Test
    public void registerComponent() {
        boolean flagA = service.registerComponent(ComponentA.class);
        boolean flagB = service.registerComponent(ComponentB.class);
        boolean flagAA = service.registerComponent(ComponentA.class);

        XComponentType cTypeA = service.components.get(0).a;
        XComponentType cTypeB = service.components.get(1).a;

        Truth.assertThat(cTypeA.getIndex()).isEqualTo(0);
        Truth.assertThat(cTypeB.getIndex()).isEqualTo(1);
        Truth.assertThat(flagA).isTrue();
        Truth.assertThat(flagB).isTrue();
        Truth.assertThat(flagAA).isFalse();
    }

    @Test
    public void getComponentType() {
        service.registerComponent(ComponentA.class);
        service.registerComponent(ComponentB.class);
        service.registerComponent(ComponentC.class);

        XComponentType cTypeA = service.getComponentType(ComponentA.class);
        XComponentType cTypeB = service.getComponentType(ComponentB.class);
        XComponentType cTypeC = service.getComponentType(ComponentC.class);
        XComponentType cTypeD = service.getComponentType(ComponentD.class);


        Truth.assertThat(cTypeA).isNotNull();
        Truth.assertThat(cTypeB).isNotNull();
        Truth.assertThat(cTypeC).isNotNull();
        Truth.assertThat(cTypeD).isNull();
    }

    @Test
    public void setAndGetComponent() {
        service.registerComponent(ComponentA.class);
        service.registerComponent(ComponentB.class);
        service.registerComponent(ComponentC.class);

        ComponentA e1cA = new ComponentA();
        ComponentC e1cC = new ComponentC();

        ComponentB e2cB = new ComponentB();
        ComponentC e2cC = new ComponentC();

        XEntityImpl e1 = new XEntityImpl(0, world);
        XEntityImpl e2 = new XEntityImpl(1, world);

        service.attachComponent(e1, e1cA);
        service.attachComponent(e1, e1cC);

        service.attachComponent(e2, e2cB);
        service.attachComponent(e2, e2cC);

        ComponentA e1A = service.getComponent(e1, ComponentA.class);
        ComponentB e1B = service.getComponent(e1, ComponentB.class);
        ComponentC e1C = service.getComponent(e1, ComponentC.class);

        ComponentA e2A = service.getComponent(e2, ComponentA.class);
        ComponentB e2B = service.getComponent(e2, ComponentB.class);
        ComponentC e2C = service.getComponent(e2, ComponentC.class);

        Truth.assertThat(e1A).isNotNull();
        Truth.assertThat(e1B).isNull();
        Truth.assertThat(e1C).isNotNull();

        Truth.assertThat(e2A).isNull();
        Truth.assertThat(e2B).isNotNull();
        Truth.assertThat(e2C).isNotNull();
    }
}