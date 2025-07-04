package com.willcodes.main;

import java.awt.*;
import java.util.LinkedList;

public class Handler {
    public LinkedList<GameObject> objects = new LinkedList<GameObject>();
    public void tick() {
        for (int i = 0; i < objects.size(); i++) {
            GameObject tempObject = objects.get(i);
            tempObject.tick();
        }
    }
    public void render(Graphics g) {
        // 先绘制所有轨迹（Trail）对象，确保它们在底层
        for (int i = 0; i < objects.size(); i++) {
            GameObject tempObject = objects.get(i);
            if (tempObject.getId() == ID.Trail) {
                tempObject.render(g);
            }
        }
        
        // 然后绘制其他所有对象
        for (int i = 0; i < objects.size(); i++) {
            GameObject tempObject = objects.get(i);
            if (tempObject.getId() != ID.Trail) {
                tempObject.render(g);
            }
        }
    }

    public void addObject(GameObject object) {
        this.objects.add(object);
    }

    public void removeObject(GameObject object) {
        this.objects.remove(object);
    }

    public void clearObjects() {
        this.objects.clear();
    }
}
