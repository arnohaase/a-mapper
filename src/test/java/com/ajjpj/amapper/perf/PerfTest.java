package com.ajjpj.amapper.perf;

import com.ajjpj.amapper.javabean.builder.JavaBeanMapping;
import com.ajjpj.amapper.javabean.japi.JavaBeanMapper;
import com.ajjpj.amapper.javabean.japi.JavaBeanMapperBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;


//@Ignore
public class PerfTest extends Assert {
    private final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
            .withBeanMapping(JavaBeanMapping.create(A.class, A.class).withMatchingPropsMappings())
            .build();

	private A a;
	
	private static final Method get0 = mtd("getA0");
	private static final Method get1 = mtd("getA1");
	private static final Method get2 = mtd("getA2");
	private static final Method get3 = mtd("getA3");
	private static final Method get4 = mtd("getA4");
	private static final Method get5 = mtd("getA5");
	private static final Method get6 = mtd("getA6");
	private static final Method get7 = mtd("getA7");
	private static final Method get8 = mtd("getA8");
	private static final Method get9 = mtd("getA9");
	
	private static final Method set0 = mtd("setA0", String.class);
	private static final Method set1 = mtd("setA1", String.class);
	private static final Method set2 = mtd("setA2", String.class);
	private static final Method set3 = mtd("setA3", String.class);
	private static final Method set4 = mtd("setA4", String.class);
	private static final Method set5 = mtd("setA5", String.class);
	private static final Method set6 = mtd("setA6", String.class);
	private static final Method set7 = mtd("setA7", String.class);
	private static final Method set8 = mtd("setA8", String.class);
	private static final Method set9 = mtd("setA9", String.class);
	
	private static final Method getChildren = mtd("getChildren");
	
	private static final Method recExplicit = mtd(PerfTest.class, "copyARecExplicit", A.class);
	private static final Method recReflection = mtd(PerfTest.class, "copyARecReflection", A.class);
	private static final Method recPartialReflection = mtd(PerfTest.class, "copyARecPartialReflection", A.class);
	
	private static Method mtd(String name, Class<?>... parameterTypes) {
	    return mtd(A.class, name, parameterTypes);
	}
	
	private static Method mtd(Class<?> cls, String name, Class<?>... parameterTypes) {
	    try {
            return cls.getDeclaredMethod(name, parameterTypes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
	
	static class ExplicitQueueItem {
		private final Queue<ExplicitQueueItem> queue;
		private final A source;
		private final A target;
		
		public ExplicitQueueItem(Queue<ExplicitQueueItem> queue, A source, A target) {
			this.queue = queue;
			this.source = source;
			this.target = target;
		}

		public void run() {
			copyAttributesExplicit(source, target);
			for(A child: source.getChildren()) {
			    final A newChild = new A();
			    target.getChildren().add(newChild);
			    
			    queue.add(new ExplicitQueueItem(queue, child, newChild));
			}
		}
	}
	
	static class ReflectionQueueItem {
	    private final Queue<ReflectionQueueItem> queue;
	    private final Object source;
	    private final Object target;
	    
	    public ReflectionQueueItem(Queue<ReflectionQueueItem> queue, Object source, Object target) {
	        this.queue = queue;
	        this.source = source;
	        this.target = target;
	    }

        @SuppressWarnings("unchecked")
	    public void run() throws Exception {
	        copyAttributesReflection(source, target);
	        for(Object child: (Collection<?>) getChildren.invoke(source)) {
	            final A newChild = new A();
	            
	            ((Collection<Object>) getChildren.invoke(target)).add(newChild);
	            
	            queue.add(new ReflectionQueueItem(queue, child, newChild));
	        }
	    }
	}
	
	static class PartialReflectionQueueItem {
	    private final Queue<PartialReflectionQueueItem> queue;
	    private final A source;
	    private final A target;
	    
	    public PartialReflectionQueueItem(Queue<PartialReflectionQueueItem> queue, A source, A target) {
	        this.queue = queue;
	        this.source = source;
	        this.target = target;
	    }

        @SuppressWarnings("unchecked")
	    public void run() throws Exception {
	        copyAttributesExplicit(source, target);
	        for(A child: (Collection<A>) getChildren.invoke(source)) {
	            final A newChild = new A();
	            
	            ((Collection<Object>) getChildren.invoke(target)).add(newChild);
	            
	            queue.add(new PartialReflectionQueueItem(queue, child, newChild));
	        }
	    }
	}
	
	@Test
	public void testCopyQueuedExplicit() {
	    for (int i=0; i<100; i++) {
	        copyAQueuedExplicit(a);
	    }

		final long start = System.currentTimeMillis();
		final A copied = copyAQueuedExplicit(a);
		final long end = System.currentTimeMillis();
		System.out.println("explicit getter and setter calls with queue: \t" + (end - start) + "ms: \t\t" + copied);
	}

	@Test
	public void testCopyQueuedReflection() throws Exception {
	    for (int i=0; i<100; i++) {
	        copyAQueuedReflection(a);
	    }
	    
	    final long start = System.currentTimeMillis();
	    final A copied = copyAQueuedReflection(a);
	    final long end = System.currentTimeMillis();
	    System.out.println("Full reflection with queue: \t" + (end - start) + "ms: \t\t" + copied);
	}
	
	@Test
	public void testCopyQueuedPartialReflection() throws Exception {
	    for (int i=0; i<100; i++) {
	        copyAQueuedPartialReflection(a);
	    }
	    
	    final long start = System.currentTimeMillis();
	    final A copied = copyAQueuedPartialReflection(a);
	    final long end = System.currentTimeMillis();
	    System.out.println("Reflection for structure, explicit for attributes with queue: \t" + (end - start) + "ms: \t\t" + copied);
	}
	
	private A copyAQueuedExplicit(A a) {
        final Queue<ExplicitQueueItem> queue = new LinkedList<ExplicitQueueItem>();

        final A result = new A();
        queue.add(new ExplicitQueueItem(queue, a, result));
        while(queue.peek() != null) {
            queue.poll().run();
        }
        
        return result;
	}
	
	private A copyAQueuedReflection(A a) throws Exception {
	    final Queue<ReflectionQueueItem> queue = new LinkedList<ReflectionQueueItem>();
	    
	    final A result = new A();
	    queue.add(new ReflectionQueueItem(queue, a, result));
	    while(queue.peek() != null) {
	        queue.poll().run();
	    }
	    
	    return result;
	}

	private A copyAQueuedPartialReflection(A a) throws Exception {
	    final Queue<PartialReflectionQueueItem> queue = new LinkedList<PartialReflectionQueueItem>();
	    
	    final A result = new A();
	    queue.add(new PartialReflectionQueueItem(queue, a, result));
	    while(queue.peek() != null) {
	        queue.poll().run();
	    }
	    
	    return result;
	}
	
	@Test
	public void testCopyRecExplicit() throws Exception {
		for(int i=0; i<100; i++) {
			copyARecExplicit(a);
		}
		
		final long start = System.currentTimeMillis();
		final A copied = copyARecExplicit(a);
		final long end = System.currentTimeMillis();
		System.out.println("Explicit getter and setter calls with recursive descent: \t" + (end - start) + "ms: \t\t" + copied);
	}
	
	@Test
	public void testCopyRecReflection() throws Exception {
	    for(int i=0; i<100; i++) {
	        copyARecReflection(a);
	    }
	    
	    final long start = System.currentTimeMillis();
	    final A copied = copyARecReflection(a);
	    final long end = System.currentTimeMillis();
	    System.out.println("Full reflection with recursive descent: \t" + (end - start) + "ms: \t\t" + copied);
	}
	
	@Test
	public void testCopyRecPartialReflection() throws Exception {
	    for(int i=0; i<100; i++) {
	        copyARecPartialReflection(a);
	    }
	    
	    final long start = System.currentTimeMillis();
	    final A copied = copyARecPartialReflection(a);
	    final long end = System.currentTimeMillis();
	    System.out.println("Reflection for structure, accessors for attributes with recursive descent: \t" + (end - start) + "ms: \t\t" + copied);
	}

    @Test
    public void testMapper() throws Exception {
        for (int i=0; i<100; i++) {
            System.out.print(".");
            copyWithMapper(a);
        }
        System.out.println();

        final long start = System.currentTimeMillis();
        final A copied = copyWithMapper(a);
        final long end = System.currentTimeMillis();
        System.out.println("Mapper: \t" + (end - start) + "ms: \t\t" + copied);
    }


    private A copyWithMapper(A orig) {
        return mapper.map(orig, A.class);
    }

	private static void copyAttributesExplicit(A source, A target) {
	    target.setA0(source.getA0());
	    target.setA1(source.getA1());
	    target.setA2(source.getA2());
	    target.setA3(source.getA3());
	    target.setA4(source.getA4());
	    target.setA5(source.getA5());
	    target.setA6(source.getA6());
	    target.setA7(source.getA7());
	    target.setA8(source.getA8());
	    target.setA9(source.getA9());
	}

	private static void copyAttributesReflection(Object source, Object target) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	    set0.invoke(target, get0.invoke(source));
	    set1.invoke(target, get1.invoke(source));
	    set2.invoke(target, get2.invoke(source));
	    set3.invoke(target, get3.invoke(source));
	    set4.invoke(target, get4.invoke(source));
	    set5.invoke(target, get5.invoke(source));
	    set6.invoke(target, get6.invoke(source));
	    set7.invoke(target, get7.invoke(source));
	    set8.invoke(target, get8.invoke(source));
	    set9.invoke(target, get9.invoke(source));
	}
	
	private A copyARecExplicit (A orig) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final A result = new A();
		copyAttributesExplicit(orig, result);
		
		for(A child: orig.getChildren()) {
			result.getChildren().add((A) recExplicit.invoke(this, child));
		}
		
		return result;
	}

    @SuppressWarnings("unchecked")
    private A copyARecReflection (A orig) throws Exception {
	    final A result = new A();
	    copyAttributesReflection(orig, result);
	    
	    for(A child: (Collection<A>) getChildren.invoke(orig)) {
	        ((Collection<A>) getChildren.invoke(result)).add((A) recReflection.invoke(this, child));
	    }
	    
	    return result;
	}

    @SuppressWarnings("unchecked")
	private A copyARecPartialReflection (A orig) throws Exception {
	    final A result = new A();
	    copyAttributesExplicit(orig, result);
	    
	    for(A child: (Collection<A>) getChildren.invoke(orig)) {
	        ((Collection<A>) getChildren.invoke(result)).add((A) recPartialReflection.invoke(this, child));
	    }
	    
	    return result;
	}

	
	@Before
	public void initA() {
		a = createA(6, 6, 0);
	}
	
	private A createA(int depth, int width, int widthIdx) {
		final A result = new A();
		
		result.setA0(value(depth, widthIdx, 0));
		result.setA1(value(depth, widthIdx, 1));
		result.setA2(value(depth, widthIdx, 2));
		result.setA3(value(depth, widthIdx, 3));
		result.setA4(value(depth, widthIdx, 4));
		result.setA5(value(depth, widthIdx, 5));
		result.setA6(value(depth, widthIdx, 6));
		result.setA7(value(depth, widthIdx, 7));
		result.setA8(value(depth, widthIdx, 8));
		result.setA9(value(depth, widthIdx, 9));
		
		if(depth > 0) {
			for(int i=0; i<width; i++) {
				result.getChildren().add(createA(depth-1, width, i));
			}
		}
		
		return result;
	}
	
	private String value(int depth, int widthIdx, int idx) {
		return "" + depth + "-" + widthIdx + "-" + idx;
	}

}
