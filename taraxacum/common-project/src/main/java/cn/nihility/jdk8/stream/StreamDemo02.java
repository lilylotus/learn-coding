package cn.nihility.jdk8.stream;


import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * StreamDemo02
 *
 * @author clover
 * @date 2020-01-04 17:54
 */
public class StreamDemo02 {

    public static void main(String[] args) {
        Stream<String> stream = Stream.of("one", "two", "three", "four", "five");

//        String[] stringArray = stream.toArray(v -> new String[v]);
        /*String[] stringArray = stream.toArray(String[]::new);
        Arrays.asList(stringArray).forEach(System.out::println);*/

        System.out.println("====================");
//        List<String> list = stream.collect(Collectors.toList());
//        list.forEach(System.out::println);


        /*List<String> list = stream.collect(() -> new ArrayList<String>(),
                (theList, item) -> theList.add(item),
                (theList, theList1) -> theList.addAll(theList1));*/

//        stream.collect(Collectors.toList());
//        List<String> list = stream.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

//        List<String> list = stream.collect(LinkedList::new, LinkedList::add, LinkedList::addAll);

//        List<String> list = stream.collect(Collectors.toCollection(LinkedList::new));
//
//        list.forEach(System.out::println);

//        Set<String> set = stream.collect(Collectors.toCollection(TreeSet::new));
//        set.forEach(System.out::println);

        String str = stream.collect(Collectors.joining("-"));
        System.out.println(str);
    }

}
