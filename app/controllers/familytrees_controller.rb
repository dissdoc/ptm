class FamilytreesController < ApplicationController
  before_filter :authenticate_user!

  def index
    @familytrees = current_user.familytrees
  end

  def new
    @familytree = current_user.familytrees.new
  end

  def create
    @familytree = current_user.familytrees.new(params[:familytree])
    if @familytree.save!
      redirect_to familytrees_path
    else
      render :action => :new
    end
  end

  def add_parent
    parent = Familytree.find(params[:parent])
    if params[:right_node]
      parent.update_attribute(:right, params[:right_node].to_i)
    elsif params[:left_node]
      parent.update_attribute(:left, params[:left_node].to_i)
    end
    redirect_to familytrees_path
  end
end