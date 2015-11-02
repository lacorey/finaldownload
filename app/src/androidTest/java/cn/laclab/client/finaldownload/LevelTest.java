package cn.laclab.client.finaldownload;

import android.test.AndroidTestCase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sinye on 15/10/29.
 */
public class LevelTest extends AndroidTestCase{
    int level = 0;
    int maxLevel = 0;

    public void testLevel(){
        List<Person> list = new ArrayList<Person>();
        Person p10 = new Person(1);
        Person p11 = new Person(1);
        List<Person> subList = new ArrayList<Person>();
        for(int i=0;i<3;i++){
            Person subP = new Person(2);
            subList.add(subP);
        }
        p10.list = subList;

        list.add(p10);
        list.add(p11);

        Person p = new Person(0);
        p.list = list;

        int level = getLevel(p);
        Log.e("TAG","--level="+level);

    }

    public int getLevel(Person person){
        if(person.list != null && person.list.size() > 0){
            level ++;
            if(level > maxLevel){
                maxLevel = level;
            }
            for(Person p : person.list){
                getLevel(p);
            }
        }else{
            return maxLevel;
        }
        return maxLevel;
    }

    class Person{
        Person(int level){
            this.level = level;
        }
        int level;
        List<Person> list;
    }
}
