package org.jbpm.migration.util;

import org.joox.Match;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.joox.JOOX.$;
import static org.joox.JOOX.ids;

/**
 *
 * Various util functions for taking a Collection and returning a map with their associated counts
 * @author abaxter@redhat.com
 *
 * Solution from https://stackoverflow.com/questions/14260134/elegant-way-of-counting-occurrences-in-a-java-collection
 */
public class CountedMapUtil {

    static public Map<String,Integer> toCountedMap(Collection<String> lst){

        return lst.stream()
                .collect(HashMap<String,Integer>::new,
                        (map,str) ->{
                            if(!map.containsKey(str)){
                                map.put(str,1);
                            }else{
                                map.put(str,map.get(str)+1);
                            }
                        },
                        HashMap<String,Integer>::putAll);
    }

    static public Map<String,Integer> toCountedMapFromIds(Collection<Match> lst){

        return lst.stream()
                .collect(HashMap<String,Integer>::new,
                        (map,match) ->{
                            String id = match.id();
                            if(!map.containsKey(id)){
                                map.put(id,1);
                            }else{
                                map.put(id,map.get(id)+1);
                            }
                        },
                        HashMap<String,Integer>::putAll);
    }

    static public Map<String,Integer> toCountedMapFromText(Collection<Match> lst){

        return lst.stream()
                .collect(HashMap<String,Integer>::new,
                        (map,match) ->{
                            String text = match.text();
                            if(!map.containsKey(text)){
                                map.put(text,1);
                            }else{
                                map.put(text,map.get(text)+1);
                            }
                        },
                        HashMap<String,Integer>::putAll);
    }

    public static void setUniqueIds(Logger LOG, String tag, Match elements) {
        toCountedMapFromIds(elements.each()).entrySet().stream()
                .filter( e -> e.getValue() > 1)
                .forEach(e -> {
                    String id = e.getKey();
                    Integer count = e.getValue();
                    AtomicInteger idCount = new AtomicInteger(0);
                    LOG.info("Multiple {} sharing the same id {}: {}", tag, id, count);
                    elements.filter(ids(id)).forEach(
                            m -> {
                            	if (idCount.get() != 0) { //don't mess with the first element, this preserves existing references
                            	$(m).attr("id", id + "_" + + idCount.addAndGet(1)) ;
                            	} else {
                            		idCount.incrementAndGet();
                            	}
                            	;}
                    );
                });
    }
}
