class AlbumsController < ApplicationController
  before_filter :authenticate_user!, :except => [:show]
  before_filter :set_album, :only => [:destroy, :edit, :update, :set_title]
  before_filter :set_albums, :only => [:destroy, :index]

  def index
    @title_page = 'My albums'
    add_breadcrumb @title_page, ''
  end

  def show
    @album = Album.find(params[:id])
  end

  def new
    @title_page = 'Create new album'
    add_breadcrumb 'My albums', albums_path
    add_breadcrumb @title_page, ''
    @album = current_user.albums.new
  end

  def create
    @album = current_user.albums.new(params[:album])
    respond_to do |format|
      if @album.save
        flash[:success] = 'Form created successfully'
        format.html { redirect_to albums_path }
      else
        format.html { render :action => :new }
      end
    end
  end

  def destroy
    @album.destroy
    respond_to do |format|
      format.js
    end
  end

  def share
    @album = Album.find(params[:album_id])
    @album.photos.each { |photo| photo.share_photo.sharing! }
    redirect_to albums_path
  end

  def edit
    @album = current_user.albums.find(params[:id])
    @title_page = "Edit #{@album.name}"
    add_breadcrumb 'My albums', albums_path
    add_breadcrumb @title_page, ''
  end

  def update
    @album = current_user.albums.find(params[:id])
    if @album.update_attributes(params[:album])
      redirect_to albums_path
    else
      redirect_to edit_album_path(@album)
    end
  end

  def set_title
    @album = current_user.albums.find(params[:id])
    if @album.update_attributes(:thumbnail => params[:thumbnail])
      redirect_to @album
    else
      redirect_to albums_path
    end
  end

  def link_photo
    collection = current_user.albums.find(params[:album_id])
    @join = collection.photo_album_joins.new(:photo_id => params[:photo_id].to_i)
    if @join.save!
      redirect_to albums_path
    else
      redirect_to root_path
    end
  end

  def tagging
    @album = current_user.albums.find(params[:album_id])
    tags = params[:tags].split(/,\s+/)
    @album.photos.each do |photo|
      t1 = photo.tags.map(&:name)
      t2 = tags
      photo.tags = (t1 + t2).map do |name|
        Tag.find_or_create_by_name(name)
      end
    end
    redirect_to @album
  end

  protected

  def set_album
    @album ||= current_user.albums.find(params[:id])
    redirect_to root_path if @album.blank?
  end

  def set_albums
    @albums = current_user.albums.all
  end
end