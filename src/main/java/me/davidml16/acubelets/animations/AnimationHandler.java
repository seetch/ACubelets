package me.davidml16.acubelets.animations;

import me.davidml16.acubelets.Main;
import me.davidml16.acubelets.animations.normal.animation1.Animation1_Task;
import me.davidml16.acubelets.animations.normal.animation2.Animation2_Task;
import me.davidml16.acubelets.animations.normal.animation3.Animation3_Task;
import me.davidml16.acubelets.animations.normal.animation4.Animation4_Task;
import me.davidml16.acubelets.animations.seasonal.easter.AnimationEaster_Task;
import me.davidml16.acubelets.animations.seasonal.halloween.AnimationHalloween_Task;
import me.davidml16.acubelets.animations.seasonal.summer.AnimationSummer_Task;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class AnimationHandler {

    private final Main main;

    private final List<String> animations;

    private List<Animation> tasks;
    private List<Entity> entities;

    public AnimationHandler(Main main) {
        this.main = main;
        this.animations = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.entities = new ArrayList<>();
    }

    public void loadAnimations() {
        this.animations.add("animation1");
        this.animations.add("animation2");
        this.animations.add("animation3");
        this.animations.add("animation4");
        this.animations.add("summer");
        this.animations.add("easter");
        this.animations.add("halloween");
    }

    public Animation getAnimation(String animation) {
        if(!this.animations.contains(animation))
            return new Animation1_Task(main);

        if(animation.equalsIgnoreCase("animation1"))
            return new Animation1_Task(main);
        else if(animation.equalsIgnoreCase("animation2"))
            return new Animation2_Task(main);
        else if(animation.equalsIgnoreCase("animation3"))
            return new Animation3_Task(main);
        else if(animation.equalsIgnoreCase("animation4"))
            return new Animation4_Task(main);
        else if(animation.equalsIgnoreCase("summer"))
            return new AnimationSummer_Task(main);
        else if(animation.equalsIgnoreCase("easter"))
            return new AnimationEaster_Task(main);
        else if(animation.equalsIgnoreCase("halloween"))
            return new AnimationHalloween_Task(main);

        return null;
    }

    public List<Animation> getTasks() {
        return tasks;
    }

    public void setTasks(List<Animation> tasks) {
        this.tasks = tasks;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

}
