class TagsController < ApplicationController
  before_filter :authenticate_user!
  before_filter :can_delete?, :only => [:destroy]

  def create
    photo_id = params[:photo_id].to_i
    @photo = Photo.find(photo_id)

    map = params[:tags].split(/,\s*/).map
    map.each do |name|
      tag = Tag.find_or_create_by_name(name)
      tagging = Tagging.find(:all, :conditions => {:tag_id => tag, :photo_id => photo_id}).first
      unless tagging
        Tagging.create!(:user_id => current_user.id, :photo_id => photo_id, :tag_id => tag.id)
      end
    end

    UserMailer.added_tag(current_user, map, @photo).deliver

    respond_to do |format|
      format.js
    end
  end

  def destroy
    tagging = Tagging.find(:all, :conditions => {:tag_id => @tag, :photo_id => @photo}).first
    tagging.destroy

    respond_to do |format|
      format.js { render :template => 'tags/create.js.erb' }
    end
  end

  protected

  def can_delete?
    @photo =  Photo.find(params[:photo_id])
    @tag = Tag.find(params[:tag_id])

    !current_user.admin_of_photo?(@photo) && !current_user.admin_of_tag?(@photo, @tag)
  end
end