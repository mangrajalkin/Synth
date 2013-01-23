/*
 * This file is part of Synth.
 *
 * Synth is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Synth is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Synth.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

#include "Envelope.hpp"
#include <iostream>

Envelope::Envelope(double attack, double delay, double sustain, double release){
	this->attack = attack;
	attackSlope = 1.0f/attack;
	this->delay = delay;
	delaySlope = (1.0f - sustain)/delay;
	attackPlusDelay = attack + delay;
	this->sustain = sustain;
	this->release = release;
	releaseSlope = sustain/release;
	allTime = attack + delay + release;
	time = 0.0f;
	amplitude = 0.0f;
	noteIsOn = false;
}

Envelope::~Envelope(){
}

void Envelope::addTime(float time){
	if (noteIsOn && this->time < attackPlusDelay){
		this->time += time;
		if (this->time < attack)
			amplitude = this->time * attackSlope;
		else
			amplitude = 1.0 - ((this->time - attack) * delaySlope);
	} else if (!noteIsOn
					&& this->time > 0.0
					&& this->time < allTime){
		this->time += time;
		amplitude = sustain - ((this->time - attackPlusDelay) * releaseSlope);
	} else if (noteIsOn){
		amplitude = sustain;
	} else {
		amplitude = 0.0;
		this->time = 0.0;
	}
	if (amplitude > 1.0)
		amplitude = 1.0;
}