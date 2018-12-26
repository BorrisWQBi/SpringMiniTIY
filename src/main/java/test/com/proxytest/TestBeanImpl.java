package test.com.proxytest;

public class TestBeanImpl implements TestBeanInf {

    private String id;

    public TestBeanImpl(String id) {
        this.id = id;
    }

    @Override
    public void print1() {
        System.out.println("print proxy 1 " + id);
    }

    @Override
    public void print2() {
        System.out.println("print proxy 2" + id);
    }
}
