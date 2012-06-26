class GroupsController < ApplicationController
  before_filter :set_group, :except => [:index, :managed, :new, :create, :invite, :not_agree]
  before_filter :authenticate_user!, :except => [:index]
  before_filter :guest?, :only => [:invite, :not_agree]

  def index
    @groups = current_user.contained_groups.all
    @title_page = "Groups"
    add_breadcrumb @title_page, ''
  end

  def managed
    @groups = current_user.managing_groups.all
    @title_page = "Managing groups"
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
    @title_page = "Edit group"
    add_breadcrumb "Group", groups_path
    add_breadcrumb @title_page, ''
  end

  def update
    if @group.update_attributes(params[:group])
      redirect_to groups_path
    else
      render :action => :edit
    end
  end

  def show
    @group_admins = @group.admins
    @group_members = @group.members
    @group_candidates = @group.candidates
    @title_page = @group.name
    @dashboards = @group.dashboards.all
    @photos = @group.link_photos.all
    add_breadcrumb "Groups", groups_path
    add_breadcrumb @title_page, ''
  end

  def join
    @group_join = @group.group_joins.new(:user => current_user, :role => 'member', :agree => true)
    if @group_join.save!
      redirect_to groups_path
    else
      render :action => :show
    end
  end

  def users
    @users = User.all
    @title_page = @group.name
    add_breadcrumb "Group", groups_path
    add_breadcrumb @title_page
  end

  def invite
    join = get_group_invite(params)
    join.invite! if join.present?

    you = User.find(params[:id])
    UserMailer.invited_photo(current_user, you, @group).deliver
    redirect_to profile_path
  end

  def not_agree
    join = get_group_invite(params)
    join.destroy if join.present?

    redirect_to profile_path
  end

  def photos
    @photos = current_user.photos.all
  end

  def link_photo
    if can_link_photo?
      @photo = Photo.find(params[:photo_id])
      if @photo.user_id == current_user.id &&
        @photo_group_join = @group.photo_group_joins.new(
            :photo_id => @photo.id,
            :user_id => current_user.id,
            :status => true)
      else
        @photo_group_join = @group.photo_group_joins.new(
            :photo_id => @photo.id,
            :user_id => current_user.id)
      end

      if @photo_group_join.save!
        UserMailer.invited_photo(current_user, @group, @photo).deliver
        redirect_to @group
      else
        render :action => :photos
      end
    else
      redirect_to @group
    end
  end

  def unlink_photo
    @photo_group_join = @group.photo_group_joins.where(:photo_id => params[:photo_id]).first
    @photo_group_join.destroy
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

  def set_group
    @group = Group.find(params[:group_id] ? params[:group_id] : params[:id])
  end

  def can_link_photo?
    @group.photo_group_joins.where(:photo_id => params[:photo_id]).first.blank?
  end
end