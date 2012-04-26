class GroupsController < ApplicationController
  before_filter :authenticate_user!, :manager?, :except => [:index]

  def index
    @groups = Group.find(:all)
    @title_page = "Groups"
    add_breadcrumb @title_page, ''
  end

  def new
    @group = current_user.managing_groups.new
    @title_page = "Create group"
    add_breadcrumb "Group", groups_path
    add_breadcrumb @title_page, ''
  end

  def create
    @group = current_user.managing_groups.new(params[:group])
    if @group.save!
      @group.group_joins.create!(:user => current_user, :role => 'admin', :accepted => true)
      redirect_to groups_path
    else
      render :action => :new
    end
  end

  def edit
    @group = Group.find(params[:id])
    @title_page = "Edit group"
    add_breadcrumb "Group", groups_path
    add_breadcrumb @title_page, ''
  end

  def update
    @group = Group.find(params[:id])
    if @group.update_attributes(params[:group])
      redirect_to groups_path
    else
      render :action => :edit
    end
  end

  def show
    @group = Group.find(params[:id])
    @title_page = @group.name
    add_breadcrumb "Groups", groups_path
    add_breadcrumb @title_page, ''
  end
end