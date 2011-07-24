/*******************************************************************************
 * Copyright 2010 Olaf Sebelin
 * 
 * This file is part of Verdandi.
 * 
 * Verdandi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Verdandi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Verdandi.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on 28.10.2006
 * Author: osebelin
 *
 */
package verdandi.ui.common;

public class Pair<E, F> {

  private E first;

  private F second;

  /**
   * @param first
   * @param second
   */
  public Pair(E first, F second) {
    super();
    this.first = first;
    this.second = second;
  }

  public Pair() {
    super();
    first = null;
    second = null;
  }

  /**
   * @return the first
   */
  public E getFirst() {
    return first;
  }

  /**
   * @return the second
   */
  public F getSecond() {
    return second;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Pair)) {
      return false;
    }
    Pair other = (Pair) obj;
    return first.equals(other.first) && second.equals(other.second);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return first.hashCode() + second.hashCode();
  }

  @Override
  public String toString() {
    return "pair(" + first.toString() + ", " + second.toString() + ")";
  }

  /**
   * @param first
   *          the first to set
   */
  public void setFirst(E first) {
    this.first = first;
  }

  /**
   * @param second
   *          the second to set
   */
  public void setSecond(F second) {
    this.second = second;
  }

}
