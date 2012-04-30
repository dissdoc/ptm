class GroupsController < ApplicationController
  before_filter :authenticate_user!, :except => [:index]
  before_filter :guest?, :only => [:invite, :not_agree]

  def index
    @groups = Group.all
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
      @group.group_joins.create!(:user => current_user, :role => 'admin', :accepted => true, :agree => true)
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
    @group_admins = @group.admins
    @group_members = @group.members
    @group_candidates = @group.candidates
    @title_page = @group.name
    add_breadcrumb "Groups", groups_path
    add_breadcrumb @title_page, ''
  end

  def join
    @group = Group.find(params[:group_id])
    @group_join = @group.group_joins.new(:user => current_user, :role => 'member', :agree => true)
    if @group_join.save!
      redirect_to groups_path
    else
      render :action => :show
    end
  end

  def users
    @group = Group.find(params[:group_id])
    @users = User.all
    @title_page = @group.name
    add_breadcrumb "Group", groups_path
    add_breadcrumb @title_page
  end

  def invite
    join = get_group_invite(params)
    join.invite! if join.present?
  end

  def not_agree
    join = get_group_invite(params)
    join.destroy if join.present?

    redirect_to @group
  end

  protected

  def get_group_invite(params)
    GroupJoin.where(
        :user_id => params[:id],
        :group_id => params[:group_id],
        :accepted => true,
        :role => 'member' ).first
  end

  def guest?
    @group = Group.find(params[:group_id])
    current_user.invite_of?(@group)
  end
end