class PhotosController < ApplicationController
  before_filter :authenticate_user!

  def new
    @album = Album.find(params[:album_id])
    @photo = @album.photos.new
    @photo.build_geo
    @photo.build_share_photo
  end

  def create
    @album = Album.find(params[:album_id])
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
    @album = Album.find(params[:album_id])
    @photo = @album.photos.find(params[:id])
    @notes = @photo.notes
    @note = @photo.notes.new
  end

  def edit
    @album = Album.find(params[:album_id])
    @photo = @album.photos.find(params[:id])
    if @photo.geo
      @photo.geo
    else
      @photo.build_geo
    end

    if @photo.share_photo
      @photo.share_photo
    else
      @photo.build_share_photo
    end
  end

  def update
    @album = Album.find(params[:album_id])
    @photo = @album.photos.find(params[:id])
    if @photo.update_attributes(params[:photo])
      redirect_to [@album, @photo]
    else
      render :action => "edit"
    end
  end
end