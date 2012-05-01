class PhotosController < ApplicationController
  before_filter :authenticate_user!
  before_filter :set_album

  def new
    @photo = @album.photos.new
    @photo.build_geo
    @photo.build_share_photo
  end

  def create
    @photo = @album.photos.new(params[:photo])
    @photo.user_id = current_user.id
    if @photo.save!
      Activity.create!(:user_id => current_user.id, :action => 'add', :object_name => 'photo', :object_link => album_photo_path(@album, @photo))
      redirect_to @album
    else
      redirect_to root_path
    end
  end

  def show
    @photo = @album.photos.find(params[:id])
    @notes = @photo.notes.all
    @note = @photo.notes.new
  end

  def edit
    @photo = @album.photos.find(params[:id])
    @photo.geo ? @photo.geo : @photo.build_geo
    @photo.share_photo ? @photo.share_photo : @photo.build_share_photo
  end

  def update
    @photo = @album.photos.find(params[:id])
    if @photo.update_attributes(params[:photo])
      redirect_to [@album, @photo]
    else
      render :action => "edit"
    end
  end

  def destroy
    @photo = current_user.photos.find(params[:id])
    @album = @photo.album
    @photo.destroy
    redirect_to @album
  end

  def add_note
    @photo = @album.photos.find(params[:id])
    @note = @photo.notes.new(params[:note])
    @note.user_id = current_user.id
    if @note.save!
      redirect_to [@album, @photo]
    else
      redirect_to root_path
    end
  end

  protected

    def set_album
      @album = Album.find(params[:album_id])
    end
end