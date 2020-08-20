package com.autocoding.level;

import java.io.Serializable;

public interface ILevelObject {

	public Serializable getId();

	public Serializable getPId();

	public int getLevel();
	
	public void setLevel(int level );
}
