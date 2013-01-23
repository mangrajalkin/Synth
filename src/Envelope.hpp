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

#ifndef ENVELOPE_HPP
#define ENVELOPE_HPP

class Envelope{
public:
	Envelope(double attack, double delay, double sustain, double release);
	~Envelope(void);
	
	void addTime(float time);
	
	double amplitude;
	bool noteIsOn;
private:
	double time,
			attack,
			attackSlope,
			delay,
			delaySlope,
			sustain,
			release,
			releaseSlope,
			attackPlusDelay,
			allTime;
};

#endif